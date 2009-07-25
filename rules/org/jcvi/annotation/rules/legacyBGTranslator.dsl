[when][] an = a 
[when][] has = with 
[when][] with a = with 
[when][] such that = and 
[when][] that is = and it is 
[when][]There =there 
[when][]there is a feature {featureName} and it ={featureName} : Feature( ) and {featureName} 
[when][]there is a feature {featureA} within {distance}{prefix:\s*kb|kbp|kbps} of {featureB}=there is a feature {featureA} within {distance}*1000 bps of {featureB}
[when][]there is a feature {featureA} within {distance}{prefix:\s*bp|bps} of {featureB}{space:\s*}={featureA} : Feature(source == {featureB}.source, start <=({featureB}.getEnd() + ({distance})), end >= ({featureB}.getStart() - ({distance})) )
[when][]there is a feature {featureName}={featureName} : Feature( )

[when][]{feature} with taxon {taxonName}=Taxon( parentNames contains "{taxonName}") from {feature}.taxon
[when][]there is a taxon {taxonName}={taxonName} : Taxon( )
#[when][]{feature} with taxon {taxonName} ={taxonName} : Taxon( ) from {feature}.taxon
[when][]there is a {program} hit to {subjectId}=BlastHit( program == "{program}", hitId == "{subjectId}")
[when][]{feature} with {program} hit to {hitId}=BlastHit( program == "{program}", hitId == "{hitId}", queryId == {feature}.featureId)
[when][]-with parent {taxonName}=parentNames contains "{taxonName}"
#[when][]-where query is {feature}=queryId == {feature}.featureId
[when][]-with {field} {comparator} {value}={field} {comparator} {value}
[when][]-with {field} is not {value}={field} != {value}
[when][]-with {field} is {value}={field} == {value}
[when][]-with {field} as {value}={field} == "{value}"

[then][]assert annotation on {featureName}=Annotation ann=new Annotation(); {featureName}.addAssertedAnnotation(ann);
[then][]assert annotation=Annotation ann=new Annotation();
[then][]set source {value}=ann.setSource("{value}");
[then][]set gene symbol {value}=ann.setGeneSymbol("{value}");
[then][]set common name {value}=ann.setCommonName("{value}");
[then][]set ec number {value}=ann.setEcNumber("{value}");
[then][]set assertion type {value}=ann.setAssertionType(Annotation.{value});
[then][]set specificity {value}=ann.setSpecificity(Annotation.{value});
[then][]set confidence {value}=ann.setConfidence({value});
[then][]set role ids {value}=ann.addRoleIds("{value}");
[then][]set go ids {value}=ann.addGoIds("{value}");

