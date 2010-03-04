# Query for when conditions (id, title, method, mode for confidence, and specificity)
# Eg. 2667;4-alpha-glucanotransferase/malto-oligosyltrehalose synthase;THIS_HMM_HIT[TIGR02401] && THIS_HMM_HIT[TIGR00217];OVER_EQUIV;97
select r.id, r.title, r.method, m.mode_name, r.confidence from annotation_rules r, action_modes m where r.mode_id=m.id

# Query for then conditions
# then;2127;go_ids;GO:0043571;IGNORE
select a.rule_id, a.field_name, m.mode_name, a.contents from annotation_actions a, action_modes m where m.id=a.mode_id;

# We need to capture m.mode_id, m.mode_name

select r.title,m.mode_name, r.confidence, r.method from annotation_rules r, action_modes m where r.mode_id=m.id

select a.field_name, m.mode_name, a.contents from annotation_actions a, action_modes m where m.id=a.mode_id;

annotation_rules
+------------+------------------+------+---------+------+
| NAME       | TYPE             | SIZE | DEFAULT | NULL |
+------------+------------------+------+---------+------+
| id         | numeric identity | 10   |         | NO   |
| accession  | varchar          | 25   |         | YES  |
| mode_id    | int              | 10   |         | NO   |
| title      | varchar          | 255  |         | YES  |
| method     | varchar          | 255  |         | NO   |
| assignby   | char             | 8    |         | NO   |
| confidence | float            | 15   |         | YES  |
| date       | smalldatetime    | 16   |         | NO   |
| comment    | varchar          | 1000 |         | YES  |
| keep_seq   | varchar          | 5000 |         | YES  |
+------------+------------------+------+---------+------+

action_modes
+-------------+------------------+------+---------+------+
| NAME        | TYPE             | SIZE | DEFAULT | NULL |
+-------------+------------------+------+---------+------+
| id          | numeric identity | 10   |         | NO   |
| mode_name   | varchar          | 32   |         | NO   |
| description | varchar          | 255  |         | NO   |
| rank        | int              | 10   |         | YES  |
| category    | varchar          | 32   |         | YES  |
| auto_cutoff | float            | 15   |         | YES  |
+-------------+------------------+------+---------+------+

annotation_actions
+------------+------------------+------+---------+------+
| NAME       | TYPE             | SIZE | DEFAULT | NULL |
+------------+------------------+------+---------+------+
| id         | numeric identity | 10   |         | NO   |
| rule_id    | int              | 10   |         | NO   |
| mode_id    | int              | 10   |         | NO   |
| field_name | varchar          | 32   |         | NO   |
| contents   | varchar          | 255  |         | NO   |
+------------+------------------+------+---------+------+

