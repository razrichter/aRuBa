[when][]-at =-with 
[when][] at {type:\S+} level= with {type} level 
[when][] or = || 
[when][] and = && 
[when][] an = a 
[when][] has = with 
[when][] with a = with 
[when][] such that = and 
[when][] that is = and it is 
[when][]There =there
[when][]equivalog-level=equivalog level
[when][]sub-family=subfamily
[when][] above-trusted = strong 
[when][] above_trusted = strong 
[when][] above trusted = strong 
[when][] above-noise = weak 
[when][] above_noise = weak 
[when][] above noise = weak 

# there is a feature geneA with genome property gProp --> geneA : Feature( properties contains genProp )
[when][]there is a feature {featureName:\S+} with genome property {propName}={featureName} : Feature( properties contains {propName} )

[when][]there is a feature {featureName:\S+} and it ={featureName} : Feature( ) and {featureName} 
[when][]there is a feature {featureA:\S+} within {distance}{prefix:\s*kb|kbp|kbps} of {featureB}=there is a feature {featureA} within {distance}*1000 bps of {featureB}
#[when][]there is a feature {featureA:\S+} within {distance}{prefix:\s*bp|bps} of {featureB}{space:\s*}={featureA} : Feature(source == {featureB}.source, start <=({featureB}.getEnd() + ({distance})), end >= ({featureB}.getStart() - ({distance})) )
[when][]there is a feature {featureA:\S+} within {distance}{prefix:\s*bp|bps} of {featureB}{space:\s*}={featureA} : Feature( eval({featureA}.isWithin({featureB}, {distance})) )
[when][]there is a feature {featureName:\S+}={featureName} : Feature( )
[when][]there is a taxon {taxonName}={taxonName} : Taxon( )
[when][]there is a annotation {annotName:\S+} on {featureName}={annotName} : Annotation( ) from {featureName}.assignedAnnotation
[when][]there is a annotation {annotName:\S+}={annotName} : Annotation( )


[when][]{feature:\S+} with annotation {annotName}={annotName} : Annotation( ) from {feature}.assignedAnnotation
[when][]{feature:\S+} with annotation {annotName}=Annotation( parentNames contains "{taxonName}") from {feature}.taxon
[when][]there is a taxon {taxonName}={taxonName} : Taxon( )

# taxon
[when][]{feature} with taxon {taxonName}=Taxon( parentNames contains "{taxonName}") from {feature}.taxon
[when][]there is a taxon {taxonName}={taxonName} : Taxon( )
# genome property
[when][]there is a genome property {propName:\S+} with id {propId:\S+}={propName} : GenomeProperty( this["id"]=="{propId}" )
[when][]there is a genome property {propName:\S+}={propName} : GenomeProperty( )
# property
[when][]there is a "{propKey}" property {propName:\S+}={propName}: Map( this["{propKey}"] == 1 )

# hmm hit
[when][]there is a hmm hit to {subjectId:(\S+)}=HmmHit ( hitId == "{subjectId}")

# Allow for any generic comparator specification (eg. equivalog or subfamily level)
[when][]there is a {program:\S+} hit to {subjectId:(\S+)} with {comparator}=BlastHit( program == "{program}", hitId == "{subjectId}", {comparator})

# Feature with hmm hit
[when][]{feature} with hmm hit to {hitId:(\S+)}=HmmHit( hitId == "{hitId}", queryId == {feature}.featureId )
[when][]{feature} with hmm hit to {hitId:(\S+)} with {comparator}=HmmHit( hitId == "{hitId}", queryId == {feature}.featureId, {comparator})

# Feature with blast hit
[when][]{feature} with {program:\S+} hit to {hitId:(\S+)} with {comparator}=BlastHit( program == "{program}", hitId == "{hitId}", queryId == {feature}.featureId, {comparator})

[when][homologyHit]there is a {program:\S+} hit to {subjectId:(\S+)} with {comparator}={program}HomologyHit( program == "{program}", hitId == "{subjectId}", {comparator})
[when][homologyHit]{feature} with {program:\S+} hit to {hitId:(\S+)} with {comparator}={program}HomologyHit( program == "{program}", hitId == "{hitId}", queryId == {feature}.featureId, {comparator})

# Strong or weak hits
[when][homologyHit]there is a strong {program:\S+} hit to {subjectId:(\S+)}={program}HomologyHit( program == "{program}", hitId == "{subjectId}", aboveTrustedHit == true)
[when][homologyHit]there is a weak {program:\S+} hit to {subjectId:(\S+)}={program}HomologyHit( program == "{program}", hitId == "{subjectId}", aboveNoiseHit == true)
[when][homologyHit]{feature} with strong {program:\S+} hit to {subjectId:(\S+)}={program}HomologyHit( program == "{program}", hitId == "{subjectId}", queryId == {feature}.featureId, aboveTrustedHit == true)
[when][homologyHit]{feature} with weak {program:\S+} hit to {subjectId:(\S+)}={program}HomologyHit( program == "{program}", hitId == "{subjectId}", queryId == {feature}.featureId, aboveNoiseHit == true)


[when][homologyHit]there is a {program:\S+} hit to {subjectId:(\S+)}={program}HomologyHit( program == "{program}", hitId == "{subjectId}")
[when][homologyHit]{feature} with {program:\S+} hit to {hitId:(\S+)}={program}HomologyHit( program == "{program}", hitId == "{hitId}", queryId == {feature}.featureId)
[when][homologyHit] {program:\S*blast\S*}HomologyHit=BlastHit
[when][homologyHit] {program:\S*hmm\S*}HomologyHit=HmmHit

[when][homologyHit]-with parent {taxonName}=parentNames contains "{taxonName}"
[when][homologyHit]-with GO term {goId:([A-Z]+:){1}[0-9]{6,}}=(goIds contains "{goId}")
[when][homologyHit]GO term {goId:([A-Z]+:){1}[0-9]{6,}}=(goIds contains "{goId}")
#[when][]-where query is {feature}=queryId == {feature}.featureId
[when][]-with taxon {txn}=taxon=={txn}
[when][]-with annotation {annot}=assignedAnnotation=={annot}
[when][]-with genome property {propName}=properties contains {propName}
[when][]-with property {propName}=properties contains {propName}

# equilvalog-level means hit at 80% identity, 95 % of query protein length, 95 % of target protein length, max 5 % difference in length of matched regions
[when][]equivalog level=percentIdentity>=80, queryPercentLength>=90, hitPercentLength>=95, hitQueryLengthRatio<=5
# subfamily-level means 60 % identity.
[when][]subfamily level=percentIdentity>=60

# Special keyword used for any property (Map or GenomeProperty)
[when][]key {field} {comparator} {value}=this["{field}"] {comparator} {value}

[when][]-with {field} is not {value}={field} != {value}
[when][]-with {field} is {value}={field} == {value}
[when][]-with {field:\S+} as {value}={field} == "{value}"
[when][]-with id {value:\S+}={field} == "{value}"
[when][]{field:\S+} at least {value:\S+}={field} >= {value}
[when][]-with {field} greater than {value:\S+}={field} > {value}
[when][]-with {field} {comparator} {value}={field} {comparator} {value}

# Generic catchall parameter statement
#[when][]-with {field:\S+} {value}={parameter}=={value}

#[then][]{expression} on {variable}={variable}.{expression}

[then][]assert annotation on {featureName}=Annotation ann=new Annotation(kcontext.getRule().getName()); {featureName}.addAssertedAnnotation(ann);
[then][]assert annotation {annotName} on {featureName}=Annotation {annotName}=new Annotation(kcontext.getRule().getName()); {featureName}.addAssertedAnnotation({annotName});
[then][]assert annotation {annotName}=Annotation {annotName}=new Annotation(kcontext.getRule().getName());
[then][]assert annotation=Annotation ann=new Annotation(kcontext.getRule().getName());

# properties (cannot mix mvel with dsl, so we need to handle this in Java)
# [then][]new "{propKey:\S+}" property {propName}={propName} = [ "{propKey}": 1 ];
[then][]new "{propKey:\S+}" property {propName}=Map<String,Object> {propName} = new HashMap<String,Object>(); {propName}.put("{propKey}", 1);

[then][]set source rule name on {variable}={variable}.setSource(rule name);
[then][]set source {value} on {variable}={variable}.setSource("{value}");
[then][]set gene symbol {value} on {variable}={variable}.setGeneSymbol("{value}");
[then][]set common name {value} on {variable}={variable}.setCommonName("{value}");
[then][]set ec number {value} on {variable}={variable}.setEcNumber("{value}");
[then][]set assertion type {value} on {variable}={variable}.setAssertionType(Annotation.{value});
[then][]set specificity {value} on {variable}={variable}.setSpecificity(Annotation.{value});
[then][]set confidence {value} on {variable}={variable}.setConfidence({value});
[then][]set role ids {value} on {variable}={variable}.addRoleIds("{value}");
[then][]set go ids {value} on {variable}={variable}.addGoIds("{value}");

[then][]add property {prop} to {variable}={variable}.addProperty({prop});


# I think we want to remove this approach below since 
# it assumes named variable "ann"
[then][]set source rule name=ann.setSource({value});
[then][]set source {value}=ann.setSource("{value}");
[then][]set gene symbol {value}=ann.setGeneSymbol("{value}");
[then][]set common name {value}=ann.setCommonName("{value}");
[then][]set ec number {value}=ann.setEcNumber("{value}");
[then][]set assertion type {value}=ann.setAssertionType(Annotation.{value});
[then][]set specificity {value}=ann.setSpecificity(Annotation.{value});
[then][]set confidence {value}=ann.setConfidence({value});
[then][]set role ids {value}=ann.addRoleIds("{value}");
[then][]set go ids {value}=ann.addGoIds("{value}");
[then][]print rule fired=System.out.println(kcontext.getRule().getName());
[then][]rule name=kcontext.getRule().getName()