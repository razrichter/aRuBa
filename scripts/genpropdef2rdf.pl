#!/usr/bin/env perl
use strict;
use warnings;

package PropDef; {
    use Moose;
    use Template;

    my %evaluable_properties = map {$_=>1} qw(PATHWAY SYSTEM METAPATH META-PROP);
    my $n3_template;
    {
        local $/ = undef;
        $n3_template = <main::DATA>;
    }
    has prop_def_id => is => 'ro';
    has property => is => 'ro';
    has description => is => 'ro', writer => '_set_description';
    has private => is => 'ro';
    has prop_type => is => 'ro';
    has role_id => is => 'ro';
    has prop_acc => is => 'ro';
    has ispublic => is => 'ro';
    has thresh => is => 'ro';
    has eval_method => is => 'ro';
    has sub_property => is => 'ro';

    has steps => is => 'rw', isa => 'Maybe[ArrayRef[PropStep]]', default => sub{[]};

    sub BUILD {
        _clean_description(@_);
    }

    sub _clean_description {
        my $self = shift;
        my $desc = $self->description;
        if ($desc) {
            $desc =~ s/\s+/ /sg;
            $desc =~ s/"/'/sg;
            $desc =~ s/^YES NO //;
            $desc =~ s/^\s+//;
            $desc =~ s/\s+$//;
            $self->_set_description($desc);
        }
    }
    sub is_evaluable {
        my $self = shift;
        # properties are evaluable if they are
        # a) an implemented property type
        return unless $evaluable_properties{$self->prop_type};
        # b) have required steps
        my @required_steps = grep {$_->in_rule == 1} @{$self->steps};
        my $evaluable = scalar @required_steps;
        # c) all required steps are evaluable
        foreach my $step (@required_steps) {
            if (! $step->is_evaluable) {
                $evaluable = 0;
                last;
            }
        }
        return $evaluable;
    }

    sub to_n3 {
        my $self = shift;
        my $t = Template->new();
        my $output='';
        if ($self->is_evaluable) {
           $t->process(\$n3_template,{prop=>$self}, \$output) or die "TT Error: ".$t->error."\n";
        }
        return $output;
    }

    __PACKAGE__->meta->make_immutable;
    no Moose;
}

package PropStep; {
    use Moose;
    has prop_step_id => is => 'ro';
    has prop_def_id => is => 'ro';
    has step_num => is => 'ro';
    has step_name => is => 'ro';
    has in_rule => is => 'ro';
    has branch => is => 'ro';
    has drop_name => is => 'ro';
    has prop_def_link_type => is => 'ro';

    has evs => is => 'rw', isa => 'Maybe[ArrayRef[StepEv]]', default => sub{[]};
    has prop_def => is => 'rw', weak_ref => 1;

    sub is_evaluable {
        # steps are evaluable if at least one evaluable evidence item exists
        my $self = shift;
        my @evaluable_ev = grep { $_->is_evaluable } @{$self->evs};
        return scalar(@evaluable_ev);
    }

    __PACKAGE__->meta->make_immutable;
    no Moose;
}

package StepEv; {
    use Moose;

    my %evaluable_evidence = map {$_=>1} qw(HMM GENPROP);

    has step_ev_id => is => 'ro';
    has prop_step_id => is => 'ro';
    has query => is => 'ro';
    has method => is => 'ro';
    has get_GO => is => 'ro';
    has prop_step_link_type => is => 'ro';

    has prop_step => is => 'rw', weak_ref => 1;

    sub is_evaluable {
        # evidence is evaluable if it is one we've implemented (as listed above)
        my $self = shift;
        return $evaluable_evidence{$self->method};
    }
    
    __PACKAGE__->meta->make_immutable;
    no Moose;
}


package main;
use DBI;

my $dbh = DBI->connect('dbi:Sybase:server=SYBPROD;database=common','access','access');

my $prop_q = $dbh->prepare('SELECT * FROM prop_def order by prop_def_id');
my $step_q = $dbh->prepare('SELECT * FROM prop_step WHERE prop_def_id = ?');
my $ev_q = $dbh->prepare('SELECT * FROM step_ev_link WHERE prop_step_id = ?');

$prop_q->execute();
my $props_r = $prop_q->fetchall_arrayref({});

# N3 header
print "\@prefix gp: <urn:genome_properties:instances:> .
\@prefix : <urn:genome_properties:ontology:> .
";

foreach my $prop (@$props_r) {
    $prop = PropDef->new(%$prop);
    $step_q->execute($prop->prop_def_id);
    my $steps = $step_q->fetchall_arrayref({});
    # if no steps, then stays non-evaluable
    foreach my $step (@$steps) {
        $step = PropStep->new(%$step);
#        $step->prop_def($prop);
        $ev_q->execute($step->prop_step_id);
        my $evs = $ev_q->fetchall_arrayref({});

        foreach my $ev (@$evs) {
            $ev = StepEv->new($ev);
#            $ev->prop_step($step);
        }
        $step->evs($evs);
    }
    $prop->steps($steps);
    if ($prop->is_evaluable) {
        print $prop->to_n3();
    }
}

__DATA__
gp:GenomeProperty_[% prop.prop_def_id %]
    a           :GenomeProperty;
    :id         "[% prop.prop_def_id %]";
    :accession  "[% prop.prop_acc %]";
    :category   "[% prop.prop_type %]";
    :threshold  "[% prop.thresh %]";
    :title      "[% prop.property %]";
    :definition "[% prop.description %]"
.
[%- FOREACH step IN prop.steps %]
    [%- IF step.is_evaluable %]
gp:FeatureProperty_Step_[% step.prop_step_id %]
    a           :FeatureProperty;
    :id         "[% step.prop_step_id %]";
    :title      "[% step.step_name %]";
        [%- IF step.in_rule %]
    :required_by gp:GenomeProperty_[% step.prop_def_id %]
        [%- ELSE %]
    :part_of    gp:GenomeProperty_[% step.prop_def_id %]
        [%- END %]
.
    [%- FOREACH ev IN step.evs %]
        [%- IF ev.is_evaluable %]
gp:FeatureProperty_Ev_[% ev.step_ev_id %]
    a               :FeatureProperty;
    :id             "[% ev.query %]";
    :category       "[% ev.method %]";
    :sufficient_for gp:FeatureProperty_Step_[% ev.prop_step_id %]
.
        [%- END %]
    [%- END %]
    [%- END %]
[%- END %]

