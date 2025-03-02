INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'US' AND payment_method.name = 'Bank Account'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'UK' AND payment_method.name = 'Bank Account'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'DE' AND payment_method.name = 'Bank Account'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'SA' AND payment_method.name = 'Bank Account'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'EG' AND payment_method.name = 'Bank Account'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'KE' AND payment_method.name = 'Bank Account'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'GH' AND payment_method.name = 'Bank Account'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'KE' AND payment_method.name = 'Mobile Money'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'CIV' AND payment_method.name = 'Mobile Money'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'ML' AND payment_method.name = 'Mobile Money'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'CM' AND payment_method.name = 'Mobile Money'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'SN' AND payment_method.name = 'Mobile Money'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'GH' AND payment_method.name = 'Mobile Money'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'RW' AND payment_method.name = 'Mobile Money'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'UG' AND payment_method.name = 'Mobile Money'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'ZW' AND payment_method.name = 'Mobile Money'
LIMIT 1 ON CONFLICT DO NOTHING;