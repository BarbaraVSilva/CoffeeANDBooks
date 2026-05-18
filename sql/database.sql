-- Create Database
CREATE DATABASE IF NOT EXISTS coffeebooks_db;
USE coffeebooks_db;

-- Table: GENERO_LIVRO
CREATE TABLE IF NOT EXISTS GENERO_LIVRO (
    id_genero INT AUTO_INCREMENT PRIMARY KEY,
    nome_genero VARCHAR(50) NOT NULL,
    localizacao_estante VARCHAR(50) NOT NULL
);

-- Table: LIVRO
CREATE TABLE IF NOT EXISTS LIVRO (
    id_livro INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    condicao_livro VARCHAR(20) NOT NULL,
    preco_venda DECIMAL(10,2) NOT NULL,
    estoque_atual INT NOT NULL,
    fk_genero INT NOT NULL,
    FOREIGN KEY (fk_genero) REFERENCES GENERO_LIVRO(id_genero)
);

-- Table: PRODUTO_CONSUMO
CREATE TABLE IF NOT EXISTS PRODUTO_CONSUMO (
    id_produto INT AUTO_INCREMENT PRIMARY KEY,
    nome_alimento VARCHAR(100) NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    categoria_cardapio VARCHAR(30) NOT NULL,
    disponivel BOOLEAN DEFAULT TRUE
);

-- Table: VENDA_CONSOLIDADA
CREATE TABLE IF NOT EXISTS VENDA_CONSOLIDADA (
    id_venda INT AUTO_INCREMENT PRIMARY KEY,
    data_venda DATETIME DEFAULT CURRENT_TIMESTAMP,
    valor_total DECIMAL(10,2) NOT NULL,
    forma_pagamento VARCHAR(30) NOT NULL,
    numero_mesa INT
);

-- Table: ITEM_VENDA_GERAL
CREATE TABLE IF NOT EXISTS ITEM_VENDA_GERAL (
    id_item INT AUTO_INCREMENT PRIMARY KEY,
    quantidade INT NOT NULL,
    preco_applied DECIMAL(10,2) NOT NULL,
    fk_venda INT NOT NULL,
    fk_livro INT NULL,
    fk_produto INT NULL,
    FOREIGN KEY (fk_venda) REFERENCES VENDA_CONSOLIDADA(id_venda) ON DELETE CASCADE,
    FOREIGN KEY (fk_livro) REFERENCES LIVRO(id_livro),
    FOREIGN KEY (fk_produto) REFERENCES PRODUTO_CONSUMO(id_produto)
);

-- Seed Data: GENERO_LIVRO
INSERT INTO GENERO_LIVRO (nome_genero, localizacao_estante) VALUES 
('Ficção Científica', 'Estante A1 - Corredor Azul'),
('Suspense & Thriller', 'Estante B2 - Próximo ao Café'),
('Clássicos Literários', 'Estante C1 - Parede de Madeira'),
('Filosofia', 'Mezanino - Área de Silêncio'),
('Gastronomia', 'Prateleira Suspensa - Balcão');

-- Seed Data: LIVRO (Including low stock for alerts)
INSERT INTO LIVRO (titulo, autor, condicao_livro, preco_venda, estoque_atual, fk_genero) VALUES 
('Duna', 'Frank Herbert', 'Novo', 89.90, 15, 1),
('Fundação', 'Isaac Asimov', 'Usado (Excelente)', 45.00, 2, 1), -- Alerta Amarelo
('O Iluminado', 'Stephen King', 'Novo', 65.00, 8, 2),
('Dom Casmurro', 'Machado de Assis', 'Usado (Marcas de Tempo)', 15.00, 0, 3), -- Alerta Vermelho
('A República', 'Platão', 'Novo', 42.00, 1, 4), -- Alerta Amarelo
('O Pequeno Príncipe', 'Antoine de Saint-Exupéry', 'Novo', 35.00, 20, 3);

-- Seed Data: PRODUTO_CONSUMO (Literary Themes)
INSERT INTO PRODUTO_CONSUMO (nome_alimento, preco_unitario, categoria_cardapio) VALUES 
('Shake Shake Shakespeare (Mocha)', 14.50, 'Bebidas Quentes'),
('Capuccino Hemingway', 12.00, 'Bebidas Quentes'),
('Pão de Queijo da Vila', 5.50, 'Salgados'),
('Torta Red Velvet "Duna"', 18.00, 'Doces'),
('Suco Natural "Laranja Mecânica"', 9.50, 'Bebidas Frias'),
('Sanduíche "Metamorfose"', 22.00, 'Salgados');

-- Table: USUARIO
CREATE TABLE IF NOT EXISTS USUARIO (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- ADMIN, OPERADOR
    data_ultima_senha DATE DEFAULT (CURRENT_DATE) -- Track password age
);

INSERT INTO USUARIO (username, password, role, data_ultima_senha) VALUES ('admin', 'admin123', 'ADMIN', '2026-01-01'); -- Expired for testing
INSERT INTO USUARIO (username, password, role, data_ultima_senha) VALUES ('operador', 'op123', 'OPERADOR', CURRENT_DATE);

-- Table: FORNECEDOR (Ingredients/Products)
CREATE TABLE IF NOT EXISTS FORNECEDOR (
    id_fornecedor INT AUTO_INCREMENT PRIMARY KEY,
    nome_fantasia VARCHAR(100) NOT NULL,
    cnpj VARCHAR(20),
    contato VARCHAR(50),
    tipo_produto VARCHAR(50) -- Grãos, Leite, etc.
);

INSERT INTO FORNECEDOR (nome_fantasia, cnpj, contato, tipo_produto) VALUES 
('Grãos do Brasil', '11.222.333/0001-00', '(11) 91111-2222', 'Café em Grãos'),
('Padaria Central', '44.555.666/0001-99', '(11) 93333-4444', 'Salgados e Doces');

-- Table: EDITORA (Books)
CREATE TABLE IF NOT EXISTS EDITORA (
    id_editora INT AUTO_INCREMENT PRIMARY KEY,
    nome_editora VARCHAR(100) NOT NULL,
    cidade VARCHAR(50)
);

INSERT INTO EDITORA (nome_editora, cidade) VALUES 
('Companhia das Letras', 'São Paulo'),
('Editora Record', 'Rio de Janeiro');

-- Table: EVENTO (Coffee events, Book exchange)
CREATE TABLE IF NOT EXISTS EVENTO (
    id_evento INT AUTO_INCREMENT PRIMARY KEY,
    nome_evento VARCHAR(100) NOT NULL,
    data_evento DATETIME NOT NULL,
    tipo_evento VARCHAR(30), -- TROCA_LIVROS, WORKSHOP_CAFE
    descricao TEXT
);

-- Table: DOACAO (Book donations)
CREATE TABLE IF NOT EXISTS DOACAO (
    id_doacao INT AUTO_INCREMENT PRIMARY KEY,
    nome_doador VARCHAR(100),
    data_doacao DATE DEFAULT (CURRENT_DATE),
    fk_livro INT NOT NULL,
    FOREIGN KEY (fk_livro) REFERENCES LIVRO(id_livro)
);

-- Add image_path to LIVRO and PRODUTO_CONSUMO
ALTER TABLE LIVRO ADD COLUMN image_path VARCHAR(255);
ALTER TABLE PRODUTO_CONSUMO ADD COLUMN image_path VARCHAR(255);

-- Table: CLIENTE (Customer registration and loyalty)
CREATE TABLE IF NOT EXISTS CLIENTE (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    email VARCHAR(100),
    telefone VARCHAR(20),
    pontos_fidelidade INT DEFAULT 0,
    data_nascimento DATE
);
