INSERT INTO COMMENT (Com_id, Com_description, User_id, Task_id)
VALUES

(1, 'Is the login API for the Gym system completed yet?', 2, 1),
(2, 'It is done. Please review the code on GitHub.', 5, 1),

(3, 'CSS error on the Gym workout screen UI. Please fix it.', 5, 2),
(4, 'I am fixing it. Should be done in about 30 minutes.', 3, 2),

(5, 'CTUScheduler is missing schedule conflict check logic.', 2, 3),
(6, 'I am writing the time-array traversal algorithm. Will finish by the weekend.', 5, 3),

(7, 'Update progress on the JavaFX module.', 5, 4),
(8, 'Database connection is done. Working on the statistical charts.', 3, 4),

(9, 'VocabMaster is having audio pronunciation issues.', 2, 5),
(10, 'The old API link is dead. I have switched to the new API.', 5, 5),

(11, 'The TASK_STATUS table is missing the description column.', 5, 6),
(12, 'I will run the alter table command to add it immediately.', 3, 6),

(13, 'Write Unit Tests for the Gym package pricing logic.', 2, 7),
(14, 'Currently writing them. 80% of test cases are covered.', 5, 7),

(15, 'Optimize the query for fetching the User list by Project.', 5, 8),
(16, 'I added an index to the PROJECT_JOINING table. Performance improved significantly.', 3, 8),

(17, 'Unity game build failed on Android devices.', 2, 9),
(18, 'The machine was missing JDK configuration. I have fixed it.', 5, 9),

(19, 'NOTIFICATION table does not save StU_id during updates.', 5, 10),
(20, 'Bug in the backend trigger. Let me re-check the logic.', 3, 10),

(21, 'Push the vocabulary reminder feature to the main branch.', 2, 11),
(22, 'Pushed. Please pull and test it out.', 5, 11),

(23, 'Task deadline calculation function has a time offset error.', 5, 12),
(24, 'System timezone mismatch. I have forced it to GMT+7.', 3, 12),
(25, 'CTUScheduler calendar screen loads very slowly.', 2, 13),
(26, 'Too many cells being rendered. I will switch to pagination.', 5, 13),

(27, 'Create mock data for PROJECT and TASK tables.', 5, 14),
(28, 'SQL mock data file is ready. I just sent it to the group.', 3, 14),

(29, 'Forgot password feature is not sending confirmation emails.', 2, 15),
(30, 'Mail server quota exceeded. Obtaining a new API key.', 5, 15),

(31, 'Update Java from version 11 to 17 for the project.', 5, 16),
(32, 'Updated. Re-building and testing old libraries for compatibility.', 3, 16),

(33, 'Cronjob for automatic task status updates is not running.', 2, 17),
(34, 'Incorrect cron syntax. Fixed it.', 5, 17),

(35, 'Fix application crash when deleting a Task.', 5, 18),
(36, 'Foreign key error in COMMENT table. Need to add ON DELETE CASCADE to schema.', 3, 18),

(37, 'Gym management UI is not responsive on mobile.', 2, 19),
(38, 'Using flexbox to adjust the responsive layout now.', 5, 19),

(39, 'Write README file for database setup instructions.', 5, 20),
(40, 'Finished the steps. File is in the project root directory.', 3, 20);
