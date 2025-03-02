INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'GH'), (SELECT id FROM mobile_money_options WHERE name = 'AIRTEL')
    ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'GH'), (SELECT id FROM mobile_money_options WHERE name = 'MTN')
ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'GH'), (SELECT id FROM mobile_money_options WHERE name = 'TIGO')
ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'GH'), (SELECT id FROM mobile_money_options WHERE name = 'VODAFONE')
ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'CIV'), (SELECT id FROM mobile_money_options WHERE name = 'FMM')
ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'CIV'), (SELECT id FROM mobile_money_options WHERE name = 'WAVE')
ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'CM'), (SELECT id FROM mobile_money_options WHERE name = 'FMM')
ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'KE'), (SELECT id FROM mobile_money_options WHERE name = 'Airtel Kenya (MPX)')
ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'KE'), (SELECT id FROM mobile_money_options WHERE name = 'M-Pesa (MPS)')
ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'SN'), (SELECT id FROM mobile_money_options WHERE name = 'EMONEY')
ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'SN'), (SELECT id FROM mobile_money_options WHERE name = 'FREEMONEY')
ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'SN'), (SELECT id FROM mobile_money_options WHERE name = 'ORANGEMONEY')
ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'SN'), (SELECT id FROM mobile_money_options WHERE name = 'WAVE')
ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'RW'), (SELECT id FROM mobile_money_options WHERE name = 'MPS')
ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'UG'), (SELECT id FROM mobile_money_options WHERE name = 'MPS')
ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'ML'), (SELECT id FROM mobile_money_options WHERE name = 'FMM')
ON CONFLICT DO NOTHING;

INSERT INTO country_mobile_money_options (country_id, mobile_money_option_id)
SELECT (SELECT country_id FROM countries WHERE acronym = 'ZW'), (SELECT id FROM mobile_money_options WHERE name = 'MPS')
ON CONFLICT DO NOTHING;




