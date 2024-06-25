CREATE TABLE bill (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL NOT NULL,
    creation_date_time TIMESTAMP NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE bill_items (
    id BIGSERIAL PRIMARY KEY,
    bill_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    item_price DECIMAL NOT NULL,
    CONSTRAINT fk_bill FOREIGN KEY (bill_id) REFERENCES bill (id) ON DELETE CASCADE,
    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items (id)
);