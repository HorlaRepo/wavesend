INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'NG' AND payment_method.name = 'Bank Account'
LIMIT 1 ON CONFLICT DO NOTHING;

INSERT INTO country_payment_method (country_id, payment_method_id)
SELECT country_id, id
FROM countries, payment_method
WHERE countries.acronym = 'NG' AND payment_method.name = 'USD (NG DOM Account)'
LIMIT 1 ON CONFLICT DO NOTHING;