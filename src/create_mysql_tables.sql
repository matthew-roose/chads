CREATE TABLE `game_line` (
  `id` int NOT NULL AUTO_INCREMENT,
  `week_number` int NOT NULL,
  `timestamp` bigint NOT NULL,
  `home_team` varchar(45) NOT NULL,
  `away_team` varchar(45) NOT NULL,
  `home_spread` decimal(3,1) NOT NULL,
  `home_moneyline` decimal(4,2) NOT NULL,
  `away_moneyline` decimal(4,2) NOT NULL,
  `game_total` decimal(3,1) NOT NULL,
  `home_score` int DEFAULT NULL,
  `away_score` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user` (
  `username` varchar(45) NOT NULL,
  `user_secret` varchar(45) NOT NULL,
  `first_name` varchar(45) NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `phone_number` char(12) DEFAULT NULL,
  `carrier` varchar(45) DEFAULT NULL,
  `opt_in_new_games_notification` tinyint DEFAULT NULL,
  `opt_in_missing_picks_notification` tinyint DEFAULT NULL,
  PRIMARY KEY (`username`),
  UNIQUE KEY `user_secret_UNIQUE` (`user_secret`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `supercontest_entry` (
  `username` varchar(45) NOT NULL,
  `user_secret` varchar(45) NOT NULL,
  `season_score` decimal(3,1) NOT NULL,
  `season_wins` int NOT NULL,
  `season_losses` int NOT NULL,
  `season_pushes` int NOT NULL,
  PRIMARY KEY (`username`),
  UNIQUE KEY `user_secret_UNIQUE` (`user_secret`),
  KEY `sc_entry_user_secret_fk_idx` (`user_secret`),
  CONSTRAINT `sc_entry_user_secret_fk` FOREIGN KEY (`user_secret`) REFERENCES `user` (`user_secret`),
  CONSTRAINT `sc_entry_username_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `supercontest_entry_week` (
   `id` int NOT NULL AUTO_INCREMENT,
   `username` varchar(45) NOT NULL,
   `user_secret` varchar(45) NOT NULL,
   `week_number` int NOT NULL,
   `week_score` decimal(2,1) NOT NULL,
   `week_wins` int NOT NULL,
   `week_losses` int NOT NULL,
   `week_pushes` int NOT NULL,
   `has_made_picks` tinyint DEFAULT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `username_week_number_UNIQUE` (`username`,`week_number`),
   KEY `sc_entry_week_username_fk_idx` (`username`),
   KEY `sc_entry_week_user_secret_fk_idx` (`user_secret`),
   CONSTRAINT `sc_entry_week_user_secret_fk` FOREIGN KEY (`user_secret`) REFERENCES `user` (`user_secret`),
   CONSTRAINT `sc_entry_week_username_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `supercontest_pick` (
  `id` int NOT NULL AUTO_INCREMENT,
  `entry_week_id` int NOT NULL,
  `game_id` int NOT NULL,
  `timestamp` bigint NOT NULL,
  `picked_team` varchar(45) NOT NULL,
  `opposing_team` varchar(45) NOT NULL,
  `home_team` varchar(45) NOT NULL,
  `away_team` varchar(45) NOT NULL,
  `home_spread` decimal(3,1) NOT NULL,
  `home_score` int DEFAULT NULL,
  `away_score` int DEFAULT NULL,
  `result` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sc_pick_entry_week_id_fk_idx` (`entry_week_id`),
  KEY `sc_pick_game_id_fk_idx` (`game_id`),
  CONSTRAINT `sc_pick_entry_week_id_fk` FOREIGN KEY (`entry_week_id`) REFERENCES `supercontest_entry_week` (`id`),
  CONSTRAINT `sc_pick_game_id_fk` FOREIGN KEY (`game_id`) REFERENCES `game_line` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `supercontest_pool` (
  `pool_name` varchar(45) NOT NULL,
  `creator_username` varchar(45) NOT NULL,
  `buy_in` int NOT NULL,
  `join_type` varchar(45) NOT NULL,
  `password` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`pool_name`),
  KEY `sc_pool_creator_username_fk_idx` (`creator_username`),
  CONSTRAINT `sc_pool_creator_username_fk` FOREIGN KEY (`creator_username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `supercontest_pool_entry` (
  `username` varchar(45) NOT NULL,
  `pool_name` varchar(45) NOT NULL,
  PRIMARY KEY (`username`,`pool_name`),
  KEY `sc_pool_entry_pool_name_fk_idx` (`pool_name`),
  CONSTRAINT `sc_pool_entry_pool_name_fk` FOREIGN KEY (`pool_name`) REFERENCES `supercontest_pool` (`pool_name`),
  CONSTRAINT `sc_pool_entry_username_fk` FOREIGN KEY (`username`) REFERENCES `supercontest_entry` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `sc_entry_pick_stats_helper` AS select `sew`.`username` AS `username`,count(0) AS `count`,`sp`.`picked_team` AS `picked_team`,`sp`.`result` AS `result` from (`supercontest_entry_week` `sew` join `supercontest_pick` `sp` on((`sew`.`id` = `sp`.`entry_week_id`))) where (`sp`.`result` is not null) group by `sew`.`username`,`sp`.`picked_team`,`sp`.`result` order by `sew`.`username`,`sp`.`picked_team`;

CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `sc_entry_pick_stats` AS select `sc_entry_pick_stats_helper`.`username` AS `username`,`sc_entry_pick_stats_helper`.`picked_team` AS `picked_team`,(select sum(`sq1`.`count`) from `sc_entry_pick_stats_helper` `sq1` where ((`sq1`.`picked_team` = `sc_entry_pick_stats_helper`.`picked_team`) and (`sq1`.`username` = `sc_entry_pick_stats_helper`.`username`))) AS `total`,(select `sq2`.`count` from `sc_entry_pick_stats_helper` `sq2` where ((`sq2`.`picked_team` = `sc_entry_pick_stats_helper`.`picked_team`) and (`sq2`.`result` = 'WIN') and (`sq2`.`username` = `sc_entry_pick_stats_helper`.`username`))) AS `wins`,(select `sq3`.`count` from `sc_entry_pick_stats_helper` `sq3` where ((`sq3`.`picked_team` = `sc_entry_pick_stats_helper`.`picked_team`) and (`sq3`.`result` = 'LOSS') and (`sq3`.`username` = `sc_entry_pick_stats_helper`.`username`))) AS `losses`,(select `sq4`.`count` from `sc_entry_pick_stats_helper` `sq4` where ((`sq4`.`picked_team` = `sc_entry_pick_stats_helper`.`picked_team`) and (`sq4`.`result` = 'PUSH') and (`sq4`.`username` = `sc_entry_pick_stats_helper`.`username`))) AS `pushes` from `sc_entry_pick_stats_helper` group by `sc_entry_pick_stats_helper`.`username`,`sc_entry_pick_stats_helper`.`picked_team` order by `sc_entry_pick_stats_helper`.`username`,`total` desc,`wins` desc,`pushes` desc;

CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `sc_entry_fade_stats_helper` AS select `sew`.`username` AS `username`,count(0) AS `count`,`sp`.`opposing_team` AS `faded_team`,`sp`.`result` AS `result` from (`supercontest_entry_week` `sew` join `supercontest_pick` `sp` on((`sew`.`id` = `sp`.`entry_week_id`))) where (`sp`.`result` is not null) group by `sew`.`username`,`sp`.`opposing_team`,`sp`.`result` order by `sew`.`username`,`sp`.`opposing_team`;

CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `sc_entry_fade_stats` AS select `sc_entry_fade_stats_helper`.`username` AS `username`,`sc_entry_fade_stats_helper`.`faded_team` AS `faded_team`,(select sum(`sq1`.`count`) from `sc_entry_fade_stats_helper` `sq1` where ((`sq1`.`faded_team` = `sc_entry_fade_stats_helper`.`faded_team`) and (`sq1`.`username` = `sc_entry_fade_stats_helper`.`username`))) AS `total`,(select `sq2`.`count` from `sc_entry_fade_stats_helper` `sq2` where ((`sq2`.`faded_team` = `sc_entry_fade_stats_helper`.`faded_team`) and (`sq2`.`result` = 'WIN') and (`sq2`.`username` = `sc_entry_fade_stats_helper`.`username`))) AS `wins`,(select `sq3`.`count` from `sc_entry_fade_stats_helper` `sq3` where ((`sq3`.`faded_team` = `sc_entry_fade_stats_helper`.`faded_team`) and (`sq3`.`result` = 'LOSS') and (`sq3`.`username` = `sc_entry_fade_stats_helper`.`username`))) AS `losses`,(select `sq4`.`count` from `sc_entry_fade_stats_helper` `sq4` where ((`sq4`.`faded_team` = `sc_entry_fade_stats_helper`.`faded_team`) and (`sq4`.`result` = 'PUSH') and (`sq4`.`username` = `sc_entry_fade_stats_helper`.`username`))) AS `pushes` from `sc_entry_fade_stats_helper` group by `sc_entry_fade_stats_helper`.`username`,`sc_entry_fade_stats_helper`.`faded_team` order by `sc_entry_fade_stats_helper`.`username`,`total` desc,`wins` desc,`pushes` desc;

CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `sc_public_pick_stats` AS select `sew`.`week_number` AS `week_number`,`sp`.`picked_team` AS `picked_team`,`sp`.`opposing_team` AS `opposing_team`,`sp`.`home_team` AS `home_team`,count(0) AS `times_picked`,`sp`.`home_spread` AS `home_spread`,`sp`.`home_score` AS `home_score`,`sp`.`away_score` AS `away_score`,`sp`.`result` AS `result` from (`supercontest_entry_week` `sew` join `supercontest_pick` `sp` on((`sew`.`id` = `sp`.`entry_week_id`))) group by `sew`.`week_number`,`sp`.`picked_team`,`sp`.`opposing_team`,`sp`.`home_team`,`sp`.`home_spread`,`sp`.`home_score`,`sp`.`away_score`,`sp`.`result` order by `times_picked` desc;

CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `sc_public_entry_weeks` AS select `supercontest_entry_week`.`week_number` AS `id`,`supercontest_entry_week`.`week_number` AS `username`,`supercontest_entry_week`.`week_number` AS `user_secret`,`supercontest_entry_week`.`week_number` AS `week_number`,SUM(`supercontest_entry_week`.`week_score`) AS `week_score`,sum(`supercontest_entry_week`.`week_wins`) AS `week_wins`,sum(`supercontest_entry_week`.`week_losses`) AS `week_losses`,sum(`supercontest_entry_week`.`week_pushes`) AS `week_pushes` from `supercontest_entry_week` group by `supercontest_entry_week`.`week_number`;

CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `sc_head_to_head_stats` AS select `sew`.`username` AS `username`,`sew`.`week_number` AS `week_number`,`sp`.`timestamp` AS `timestamp`,`sp`.`game_id` AS `game_id`,`sp`.`picked_team` AS `picked_team`,`sp`.`home_spread` AS `home_spread`,`sp`.`home_team` AS `home_team`,`sp`.`away_team` AS `away_team`,`sp`.`home_score` AS `home_score`,`sp`.`away_score` AS `away_score`,`sp`.`result` AS `result` from (`supercontest_entry_week` `sew` join `supercontest_pick` `sp` on((`sp`.`entry_week_id` = `sew`.`id`))) order by `sp`.`game_id`,`sew`.`username`;

CREATE TABLE `survivor_entry` (
  `username` varchar(45) NOT NULL,
  `user_secret` varchar(45) NOT NULL,
  `score` decimal(3,1) NOT NULL,
  `wins` int NOT NULL,
  `losses` int NOT NULL,
  `pushes` int NOT NULL,
  `current_streak` int NOT NULL,
  PRIMARY KEY (`username`),
  KEY `sv_entry_user_secret_fk_idx` (`user_secret`),
  CONSTRAINT `sv_entry_user_secret_fk` FOREIGN KEY (`user_secret`) REFERENCES `user` (`user_secret`),
  CONSTRAINT `sv_entry_username_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `survivor_pick` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `user_secret` varchar(45) NOT NULL,
  `game_id` int NOT NULL,
  `week_number` int NOT NULL,
  `timestamp` bigint NOT NULL,
  `picked_team` varchar(45) NOT NULL,
  `opposing_team` varchar(45) NOT NULL,
  `home_team` varchar(45) NOT NULL,
  `away_team` varchar(45) NOT NULL,
  `home_score` int DEFAULT NULL,
  `away_score` int DEFAULT NULL,
  `result` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sv_pick_username_fk_idx` (`username`),
  KEY `sv_pick_user_secret_fk_idx` (`user_secret`),
  KEY `sv_pick_game_id_fk_idx` (`game_id`),
  CONSTRAINT `sv_pick_user_secret_fk` FOREIGN KEY (`user_secret`) REFERENCES `user` (`user_secret`),
  CONSTRAINT `sv_pick_username_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`),
  CONSTRAINT `sv_pick_game_id_fk` FOREIGN KEY (`game_id`) REFERENCES `game_line` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `survivor_pool` (
  `pool_name` varchar(45) NOT NULL,
  `creator_username` varchar(45) NOT NULL,
  `buy_in` int NOT NULL,
  `join_type` varchar(45) NOT NULL,
  `password` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`pool_name`),
  KEY `sv_pool_creator_username_fk_idx` (`creator_username`),
  CONSTRAINT `sv_pool_creator_username_fk` FOREIGN KEY (`creator_username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `survivor_pool_entry` (
  `username` varchar(45) NOT NULL,
  `pool_name` varchar(45) NOT NULL,
  PRIMARY KEY (`username`,`pool_name`),
  KEY `sv_pool_entry_pool_name_fk_idx` (`pool_name`),
  CONSTRAINT `sv_pool_entry_pool_name_fk` FOREIGN KEY (`pool_name`) REFERENCES `survivor_pool` (`pool_name`),
  CONSTRAINT `sv_pool_entry_username_fk` FOREIGN KEY (`username`) REFERENCES `survivor_entry` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `sv_public_pick_stats` AS select week_number, picked_team, opposing_team, home_team, count(0) AS `times_picked`, home_score, away_score, result from (survivor_pick) group by week_number, picked_team, opposing_team, home_team, home_score, away_score, result order by times_picked desc;

CREATE TABLE `sportsbook_account` (
  `username` varchar(45) NOT NULL,
  `user_secret` varchar(45) NOT NULL,
  `available_balance` decimal(20,2) NOT NULL,
  `pending_balance` decimal(20,2) NOT NULL,
  `deposit_total` int NOT NULL,
  `cash_out_total` int NOT NULL,
  `win_loss_total` decimal(20,2) NOT NULL,
  `best_parlay_odds` decimal(20,2) NOT NULL,
  PRIMARY KEY (`username`),
  KEY `sb_account_user_secret_fk_idx` (`user_secret`),
  CONSTRAINT `sb_account_user_secret_fk` FOREIGN KEY (`user_secret`) REFERENCES `user` (`user_secret`),
  CONSTRAINT `sb_account_username_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sportsbook_bet` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `user_secret` varchar(45) NOT NULL,
  `placed_timestamp` bigint NOT NULL,
  `week_number` int NOT NULL,
  `bet_type` varchar(45) NOT NULL,
  `teaser_points` decimal(3,1) DEFAULT NULL,
  `odds` decimal(20,5) NOT NULL,
  `effective_odds` decimal(20,5) NOT NULL,
  `wager` decimal(10,2) NOT NULL,
  `to_win_amount` decimal(10,2) NOT NULL,
  `effective_to_win_amount` decimal(10,2) NOT NULL,
  `result` varchar(45) DEFAULT NULL,
  `profit` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sb_bet_username_fk_idx` (`username`),
  KEY `sb_bet_user_secret_fk_idx` (`user_secret`),
  CONSTRAINT `sb_bet_user_secret_fk` FOREIGN KEY (`user_secret`) REFERENCES `user` (`user_secret`),
  CONSTRAINT `sb_bet_username_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sportsbook_bet_leg` (
  `id` int NOT NULL AUTO_INCREMENT,
  `bet_id` int NOT NULL,
  `game_id` int NOT NULL,
  `timestamp` bigint NOT NULL,
  `bet_leg_type` varchar(45) NOT NULL,
  `bought_points` decimal(2,1) DEFAULT NULL,
  `odds` decimal(7,5) NOT NULL,
  `home_spread` decimal(3,1) NOT NULL,
  `game_total` decimal(3,1) NOT NULL,
  `home_team` varchar(45) NOT NULL,
  `away_team` varchar(45) NOT NULL,
  `home_score` int DEFAULT NULL,
  `away_score` int DEFAULT NULL,
  `result` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sb_bet_leg_bet_id_fk` (`bet_id`),
  KEY `sb_bet_leg_game_id_fk_idx` (`game_id`),
  CONSTRAINT `sb_bet_leg_bet_id_fk` FOREIGN KEY (`bet_id`) REFERENCES `sportsbook_bet` (`id`),
  CONSTRAINT `sb_bet_leg_game_id_fk` FOREIGN KEY (`game_id`) REFERENCES `game_line` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sportsbook_pool` (
   `pool_name` varchar(45) NOT NULL,
   `creator_username` varchar(45) NOT NULL,
   `buy_in` int NOT NULL,
   `win_loss_prize_pct` int NOT NULL,
   `best_parlay_prize_pct` int NOT NULL,
   `join_type` varchar(45) NOT NULL,
   `password` varchar(45) DEFAULT NULL,
   PRIMARY KEY (`pool_name`),
   KEY `sb_pool_creator_username_fk_idx` (`creator_username`),
   CONSTRAINT `sb_pool_creator_username_fk` FOREIGN KEY (`creator_username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sportsbook_pool_entry` (
     `username` varchar(45) NOT NULL,
     `pool_name` varchar(45) NOT NULL,
     PRIMARY KEY (`username`,`pool_name`),
     KEY `sb_pool_entry_pool_name_fk_idx` (`pool_name`),
     CONSTRAINT `sb_pool_entry_pool_name_fk` FOREIGN KEY (`pool_name`) REFERENCES `sportsbook_pool` (`pool_name`),
     CONSTRAINT `sb_pool_entry_username_fk` FOREIGN KEY (`username`) REFERENCES `sportsbook_account` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


# not used
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `sb_every_bet_and_bet_leg` AS select `sb`.`username` AS `username`,`sb`.`week_number` AS `week_number`,`sb`.`bet_type` AS `bet_type`,`sb`.`result` AS `bet_result`,`sb`.`wager` AS `wager`,`sb`.`profit` AS `profit`,`sbl`.`game_id` AS `game_id`,`sbl`.`bet_leg_type` AS `bet_leg_type`,`sbl`.`odds` AS `odds`,`sbl`.`home_team` AS `home_team`,`sbl`.`away_team` AS `away_team`,`sbl`.`result` AS `bet_leg_result` from (`sportsbook_bet` `sb` join `sportsbook_bet_leg` `sbl` on((`sbl`.`bet_id` = `sb`.`id`)));

CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `sb_weekly_user_stats` AS select `oq`.`week_number` AS `week_number`,`oq`.`username` AS `username`,(select sum(`sq1`.`wager`) from `sportsbook_bet` `sq1` where ((`sq1`.`username` = `oq`.`username`) and (`sq1`.`week_number` = `oq`.`week_number`))) AS `amount_wagered`,(select sum(`sq2`.`effective_to_win_amount`) from `sportsbook_bet` `sq2` where ((`sq2`.`username` = `oq`.`username`) and (`sq2`.`week_number` = `oq`.`week_number`) and (`sq2`.`result` = 'WIN'))) AS `amount_won`,(select (sum(`sq3`.`wager`) * -(1)) from `sportsbook_bet` `sq3` where ((`sq3`.`username` = `oq`.`username`) and (`sq3`.`week_number` = `oq`.`week_number`) and (`sq3`.`result` = 'LOSS'))) AS `amount_lost`,(select sum(`sq4`.`profit`) from `sportsbook_bet` `sq4` where ((`sq4`.`username` = `oq`.`username`) and (`sq4`.`week_number` = `oq`.`week_number`))) AS `profit`,(select max(`sq5`.`effective_odds`) from `sportsbook_bet` `sq5` where ((`sq5`.`username` = `oq`.`username`) and (`sq5`.`week_number` = `oq`.`week_number`) and (`sq5`.`bet_type` = 'PARLAY') and (`sq5`.`result` = 'WIN'))) AS `best_parlay_odds` from `sportsbook_bet` `oq` group by `oq`.`username`,`oq`.`week_number` order by `oq`.`week_number`,`profit` desc;
