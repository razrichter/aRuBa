
[when][]There is a {program} hit to {subjectId}=feat:Feature( $queryId : featureId );$hit : BlastHit( program == "{program}", hitId == "{subjectId}",  queryId == $queryId)
[when][]-with {field} {comparator} {value}={field} {comparator} {value}
[then][]assert annotation=Annotation ann=new Annotation(); feat.addAssertedAnnotation(ann);
[then][]set source {value}=ann.setSource("{value}");
[then][]set gene symbol {value}=ann.setGeneSymbol("{value}");
[then][]set common name {value}=ann.setCommonName("{value}");
[then][]set ec number {value}=ann.setEcNumber("{value}");
[then][]set assertion type {value}=ann.setAssertionType(Annotation.{value});
[then][]set specificity {value}=ann.setSpecificity(Annotation.{value});
[then][]set confidence {value}=ann.setConfidence({value});
[then][]set role ids {value}=ann.addRoleIds("{value}");
[then][]set go ids {value}=ann.addGoIds("{value}");

