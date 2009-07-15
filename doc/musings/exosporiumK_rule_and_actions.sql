select r.title,m.mode_name, r.confidence,  r.method
from annotation_rules r
JOIN action_modes m ON r.mode_id = m.id
where r.title='exosporium protein K';

select a.field_name, m.mode_name, a.contents
from annotation_actions a 
join action_modes m on m.id = a.mode_id
where a.rule_id = 2388;
