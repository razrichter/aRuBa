[when][]-at =-with 
[when][] at = with 
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

[when][]{feature} with taxon {taxonName}=Taxon( parentNames contains "{taxonName}") from {feature}.taxon
[when][]there is a taxon {taxonName}={taxonName} : Taxon( )

#[when][]{feature} with taxon {taxonName} ={taxonName} : Taxon( ) from {feature}.taxon

# Allow for any generic comparator specification (eg. equivalog or subfamily level)
[when][]there is a {program:\S+} hit to {subjectId:(\S+)} with {comparator}=BlastHit( program == "{program}", hitId == "{subjectId}", {comparator})
[when][]{feature} with {program:\S+} hit to {hitId:(\S+)} with {comparator}=BlastHit( program == "{program}", hitId == "{hitId}", queryId == {feature}.featureId, {comparator})

[when][]there is a {program:\S+} hit to {subjectId:(\S+)}=BlastHit( program == "{program}", hitId == "{subjectId}")
[when][]{feature} with {program:\S+} hit to {hitId:(\S+)}=BlastHit( program == "{program}", hitId == "{hitId}", queryId == {feature}.featureId)
[when][]-with parent {taxonName}=parentNames contains "{taxonName}"
#[when][]-with GO term {goId1} {conjunction:\s*and|or} {goId2}=(goIds contains "{goId1}") {conjunction} (goIds contains "{goId2})"
[when][]-with GO term {goId:([A-Z]+:){1}[0-9]{6,}}=(goIds contains "{goId}")
[when][]GO term {goId:([A-Z]+:){1}[0-9]{6,}}=(goIds contains "{goId}")
#[when][]-where query is {feature}=queryId == {feature}.featureId
[when][]-with taxon {txn}=taxon=={txn}
[when][]-with annotation {annot}=assignedAnnotation=={annot}

# equilvalog-level means hit at 80% identity, 95 % of query protein length, 95 % of target protein length, max 5 % difference in length of matched regions
[when][]equivalog level=percentIdentity>=80, queryPercentLength>=90, hitPercentLength>=95, hitQueryLengthRatio<=5
# subfamily-level means 60 % identity.
[when][]subfamily level=percentIdentity>=60
[when][]-with {field} {comparator} {value}={field} {comparator} {value}
[when][]-with {field} is not {value}={field} != {value}
[when][]-with {field} is {value}={field} == {value}
[when][]-with {field} as {value}={field} == "{value}"

#[then][]{expression} on {variable}={variable}.{expression}
[then][]assert annotation on {featureName}=Annotation ann=new Annotation(kcontext.getRule().getName()); {featureName}.addAssertedAnnotation(ann);
[then][]assert annotation {annotName}=Annotation {annotName}=new Annotation(kcontext.getRule().getName());
[then][]assert annotation=Annotation ann=new Annotation(kcontext.getRule().getName());
[then][]set source {value} on {variable}={variable}.setSource("{value}");
[then][]set gene symbol {value} on {variable}={variable}.setGeneSymbol("{value}");
[then][]set common name {value} on {variable}={variable}.setCommonName("{value}");
[then][]set ec number {value} on {variable}={variable}.setEcNumber("{value}");
[then][]set assertion type {value} on {variable}={variable}.setAssertionType(Annotation.{value});
[then][]set specificity {value} on {variable}={variable}.setSpecificity(Annotation.{value});
[then][]set confidence {value} on {variable}={variable}.setConfidence({value});
[then][]set role ids {value} on {variable}={variable}.addRoleIds("{value}");
[then][]set go ids {value} on {variable}={variable}.addGoIds("{value}");

# I think we want to remove this approach below since 
# it assumes named variable "ann"
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
