INSERT INTO countries (country_id, rating, acronym, currency, name) VALUES
                                                                        (1, 3, 'NG', 'NGN', 'Nigeria'),
                                                                        (2, 5, 'US', 'USD', 'United States'),
                                                                        (3, 3, 'SA', 'ZAR', 'South Africa'),
                                                                        (4, 3, 'EG', 'EGP', 'Egypt'),
                                                                        (5, 3, 'KE', 'KES', 'Kenya'),
                                                                        (6, 3, 'GH', 'GHS', 'Ghana'),
                                                                        (7, 3, 'CIV', 'XOF', 'Ivory Coast'),
                                                                        (8, 3, 'ML', 'XOF', 'Mali'),
                                                                        (9, 3, 'CM', 'XAF', 'Cameroon'),
                                                                        (10, 3, 'SN', 'XOF', 'Senegal'),
                                                                        (11, 3, 'RW', 'RWF', 'Rwanda'),
                                                                        (12, 3, 'UG', 'UGX', 'Uganda'),
                                                                        (13, 3, 'ZW', 'ZMW', 'Zimbabwe'),
                                                                        (14, 5, 'UK', 'GBP', 'United Kingdom'),
                                                                        (15, 5, 'DE', 'EUR', 'Germany')
ON CONFLICT DO NOTHING;