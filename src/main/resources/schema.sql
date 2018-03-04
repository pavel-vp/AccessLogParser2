DROP TABLE IF EXISTS logdata;

CREATE TABLE logdata  (
    accessdate DATETIME,
    ip VARCHAR(255),
    request VARCHAR(255),
    status VARCHAR(20),
    useragent VARCHAR(2000)
);

DROP TABLE IF EXISTS logdataresult;

CREATE TABLE logdataresult  (
    ip VARCHAR(255),
    params VARCHAR(2000)
);