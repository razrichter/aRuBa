[condition][]-at=-with
[condition][]at {type:\S+} level=with {type} level
[condition][]or=||
[condition][]and=&&
[condition][]an=a
[condition][]has=with
[condition][]with a=with
[condition][]such that=and
[condition][]that is=and it is
[condition][]There=there
[condition][]equivalog-level=equivalog level
[condition][]sub-family=subfamily
[condition][]above-trusted=strong
[condition][]above_trusted=strong
[condition][]above trusted=strong
[condition][]above-noise=weak
[condition][]above_noise=weak
[condition][]above noise=weak
[condition][]there is a feature {featureName:\S+} with genome property {propName}={featureName} : Feature( properties contains {propName} )
[condition][]there is a feature {featureName:\S+} and it={featureName} : Feature( ) and {featureName} 
[condition][]there is a feature {featureA:\S+} within {distance}{prefix:\s*kb|kbp|kbps} of {featureB}=there is a feature {featureA} within {distance}*1000 bps of {featureB}
[condition][]there is a feature {featureA:\S+} within {distance}{prefix:\s*bp|bps} of {featureB}{space:\s*}={featureA} : Feature( eval({featureA}.isWithin({featureB}, {distance})) )
[condition][]there is a feature {featureName:\S+}={featureName} : Feature( )
[condition][]there is a taxon {taxonName}={taxonName} : Taxon( )
[condition][]there is a annotation {annotName:\S+} on {featureName}={annotName} : Annotation( ) from {featureName}.assignedAnnotation
[condition][]there is a annotation {annotName:\S+}={annotName} : Annotation( )
[condition][]{feature:\S+} with annotation {annotName}={annotName} : Annotation( ) from {feature}.assignedAnnotation
[condition][]{feature:\S+} with annotation {annotName}=Annotation( parentNames contains "{taxonName}") from {feature}.taxon
[condition][]there is a taxon {taxonName}={taxonName} : Taxon( )
[condition][]{feature} with taxon {taxonName}=Taxon( parentNames contains "{taxonName}") from {feature}.taxon
[condition][]there is a taxon {taxonName}={taxonName} : Taxon( )
[condition][]there is a genome property {propName:\S+} with id {propId:\S+}={propName} : GenomeProperty( this["id"]=="{propId}" )
[condition][]there is a genome property {propName:\S+}={propName} : GenomeProperty( )
[condition][]there is a "{propKey}" property {propName:\S+}={propName}: Map( this["{propKey}"] == 1 )
[condition][]there is a hmm hit to {subjectId:(\S+)}=HmmHit ( hitId == "{subjectId}")
[condition][]there is a {program:\S+} hit to {subjectId:(\S+)} with {comparator}=BlastHit( program == "{program}", hitId == "{subjectId}", {comparator})
[condition][]{feature} with hmm hit to {hitId:(\S+)}=HmmHit( hitId == "{hitId}", queryId == {feature}.featureId )
[condition][]{feature} with hmm hit to {hitId:(\S+)} with {comparator}=HmmHit( hitId == "{hitId}", queryId == {feature}.featureId, {comparator})
[condition][]{feature} with {program:\S+} hit to {hitId:(\S+)} with {comparator}=BlastHit( program == "{program}", hitId == "{hitId}", queryId == {feature}.featureId, {comparator})
[condition][homologyHit]there is a {program:\S+} hit to {subjectId:(\S+)} with {comparator}={program}HomologyHit( program == "{program}", hitId == "{subjectId}", {comparator})
[condition][homologyHit]{feature} with {program:\S+} hit to {hitId:(\S+)} with {comparator}={program}HomologyHit( program == "{program}", hitId == "{hitId}", queryId == {feature}.featureId, {comparator})
[condition][homologyHit]there is a strong {program:\S+} hit to {subjectId:(\S+)}={program}HomologyHit( program == "{program}", hitId == "{subjectId}", aboveTrustedHit == true)
[condition][homologyHit]there is a weak {program:\S+} hit to {subjectId:(\S+)}={program}HomologyHit( program == "{program}", hitId == "{subjectId}", aboveNoiseHit == true)
[condition][homologyHit]{feature} with strong {program:\S+} hit to {subjectId:(\S+)}={program}HomologyHit( program == "{program}", hitId == "{subjectId}", queryId == {feature}.featureId, aboveTrustedHit == true)
[condition][homologyHit]{feature} with weak {program:\S+} hit to {subjectId:(\S+)}={program}HomologyHit( program == "{program}", hitId == "{subjectId}", queryId == {feature}.featureId, aboveNoiseHit == true)
[condition][homologyHit]there is a {program:\S+} hit to {subjectId:(\S+)}={program}HomologyHit( program == "{program}", hitId == "{subjectId}")
[condition][homologyHit]{feature} with {program:\S+} hit to {hitId:(\S+)}={program}HomologyHit( program == "{program}", hitId == "{hitId}", queryId == {feature}.featureId)
[condition][homologyHit] {program:\S*blast\S*}HomologyHit=BlastHit
[condition][homologyHit] {program:\S*hmm\S*}HomologyHit=HmmHit
[condition][homologyHit]-with parent {taxonName}=parentNames contains "{taxonName}"
[condition][homologyHit]-with GO term {goId:([A-Z]+:){1}[0-9]{6,}}=(goIds contains "{goId}")
[condition][homologyHit]GO term {goId:([A-Z]+:){1}[0-9]{6,}}=(goIds contains "{goId}")
[condition][]-with taxon {txn}=taxon=={txn}
[condition][]-with annotation {annot}=assignedAnnotation=={annot}
[condition][]-with genome property {propName}=properties contains {propName}
[condition][]-with property {propName}=properties contains {propName}
[condition][]equivalog level=percentIdentity>=80, queryPercentLength>=90, hitPercentLength>=95, hitQueryLengthRatio<=5
[condition][]subfamily level=percentIdentity>=60
[condition][]key {field}  {comparator}  {value}=this["{field}"] {comparator}  {value}
[condition][]-with {field} is not {value}={field} != {value}
[condition][]-with {field} is {value}={field} == {value}
[condition][]-with {field:\S+} as {value}={field} == "{value}"
[condition][]-with id {value:\S+}={field} == "{value}"
[condition][]{field:\S+} at least {value:\S+}={field} >= {value}
[condition][]-with {field} greater than {value:\S+}={field} > {value}
[condition][]-with {field}  {comparator}  {value}={field}  {comparator}  {value}
[consequence][]assert annotation on {featureName}=Annotation ann=new Annotation(kcontext.getRule().getName()); {featureName}.addAssertedAnnotation(ann);
[consequence][]assert annotation {annotName} on {featureName}=Annotation {annotName}=new Annotation(kcontext.getRule().getName()); {featureName}.addAssertedAnnotation({annotName});
[consequence][]assert annotation {annotName}=Annotation {annotName}=new Annotation(kcontext.getRule().getName());
[consequence][]assert annotation=Annotation ann=new Annotation(kcontext.getRule().getName());
[consequence][]new "{propKey:\S+}" property {propName}=Map<String,Object> {propName} = new HashMap<String,Object>(); {propName}.put("{propKey}", 1);
[consequence][]set source rule name on {variable}={variable}.setSource(rule name);
[consequence][]set source {value} on {variable}={variable}.setSource("{value}");
[consequence][]set gene symbol {value} on {variable}={variable}.setGeneSymbol("{value}");
[consequence][]set common name {value} on {variable}={variable}.setCommonName("{value}");
[consequence][]set ec number {value} on {variable}={variable}.setEcNumbers("{value}");
[consequence][]set assertion type {value} on {variable}={variable}.setAssertionType(Annotation.{value});
[consequence][]set specificity {value} on {variable}={variable}.setSpecificity(Annotation.{value});
[consequence][]set confidence {value} on {variable}={variable}.setConfidence({value});
[consequence][]set role ids {value} on {variable}={variable}.addRoleIds("{value}");
[consequence][]set go ids {value} on {variable}={variable}.addGoIds("{value}");
[consequence][]add property {prop} to {variable}={variable}.addProperty({prop});
[consequence][]set source rule name=ann.setSource({value});
[consequence][]set source {value}=ann.setSource("{value}");
[consequence][]set gene symbol {value}=ann.setGeneSymbol("{value}");
[consequence][]set common name {value}=ann.setCommonName("{value}");
[consequence][]set ec number {value}=ann.setEcNumbers("{value}");
[consequence][]set assertion type {value}=ann.setAssertionType(Annotation.{value});
[consequence][]set specificity {value}=ann.setSpecificity(Annotation.{value});
[consequence][]set confidence {value}=ann.setConfidence({value});
[consequence][]set role ids {value}=ann.addRoleIds("{value}");
[consequence][]set go ids {value}=ann.addGoIds("{value}");
[consequence][]print rule fired=System.out.println(kcontext.getRule().getName());
[consequence][]rule name=kcontext.getRule().getName()
