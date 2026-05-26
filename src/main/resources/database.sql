-- Create Database
CREATE DATABASE IF NOT EXISTS coffeebooks_db;
USE coffeebooks_db;

-- 1. Table: GENERO_LIVRO
CREATE TABLE IF NOT EXISTS GENERO_LIVRO (
    id_genero INT AUTO_INCREMENT PRIMARY KEY,
    nome_genero VARCHAR(50) NOT NULL,
    localizacao_estante VARCHAR(50) NOT NULL
);

-- 2. Table: EDITORA (Books)
CREATE TABLE IF NOT EXISTS EDITORA (
    id_editora INT AUTO_INCREMENT PRIMARY KEY,
    nome_editora VARCHAR(100) NOT NULL,
    cidade VARCHAR(50)
);

-- 3. Table: FORNECEDOR (Ingredients/Products)
CREATE TABLE IF NOT EXISTS FORNECEDOR (
    id_fornecedor INT AUTO_INCREMENT PRIMARY KEY,
    nome_fantasia VARCHAR(100) NOT NULL,
    cnpj VARCHAR(20),
    contato VARCHAR(50),
    tipo_produto VARCHAR(50) -- Grãos, Leite, etc.
);

-- 4. Table: CLIENTE (Customer registration and loyalty)
CREATE TABLE IF NOT EXISTS CLIENTE (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    email VARCHAR(100),
    telefone VARCHAR(20),
    pontos_fidelidade INT DEFAULT 0,
    data_nascimento DATE
);

-- 5. Table: LIVRO
CREATE TABLE IF NOT EXISTS LIVRO (
    id_livro INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    condicao_livro VARCHAR(20) NOT NULL,
    preco_venda DECIMAL(10,2) NOT NULL,
    estoque_atual INT NOT NULL,
    fk_genero INT NOT NULL,
    fk_editora INT NULL, -- Opcional: conecta Livro à sua Editora
    image_path VARCHAR(255),
    FOREIGN KEY (fk_genero) REFERENCES GENERO_LIVRO(id_genero),
    FOREIGN KEY (fk_editora) REFERENCES EDITORA(id_editora)
);

-- 6. Table: PRODUTO_CONSUMO
CREATE TABLE IF NOT EXISTS PRODUTO_CONSUMO (
    id_produto INT AUTO_INCREMENT PRIMARY KEY,
    nome_alimento VARCHAR(100) NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    categoria_cardapio VARCHAR(30) NOT NULL,
    disponivel BOOLEAN DEFAULT TRUE,
    fk_fornecedor INT NULL, -- Opcional: conecta Produto ao Fornecedor
    image_path VARCHAR(255),
    FOREIGN KEY (fk_fornecedor) REFERENCES FORNECEDOR(id_fornecedor)
);

-- 7. Table: INGREDIENTE
CREATE TABLE IF NOT EXISTS INGREDIENTE (
    id_ingrediente INT AUTO_INCREMENT PRIMARY KEY,
    nome_ingrediente VARCHAR(100) NOT NULL,
    quantidade_atual DOUBLE NOT NULL,
    unidade_medida VARCHAR(20) NOT NULL,
    fk_fornecedor INT NULL, -- Opcional: conecta Insumo ao Fornecedor
    FOREIGN KEY (fk_fornecedor) REFERENCES FORNECEDOR(id_fornecedor)
);

-- 8. Table: VENDA_CONSOLIDADA
CREATE TABLE IF NOT EXISTS VENDA_CONSOLIDADA (
    id_venda INT AUTO_INCREMENT PRIMARY KEY,
    data_venda DATETIME DEFAULT CURRENT_TIMESTAMP,
    valor_total DECIMAL(10,2) NOT NULL,
    forma_pagamento VARCHAR(30) NOT NULL,
    numero_mesa INT,
    fk_cliente INT NULL, -- Opcional: conecta Venda ao Cliente
    FOREIGN KEY (fk_cliente) REFERENCES CLIENTE(id_cliente)
);

-- 9. Table: ITEM_VENDA_GERAL
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

-- 10. Table: DOACAO (Book donations)
CREATE TABLE IF NOT EXISTS DOACAO (
    id_doacao INT AUTO_INCREMENT PRIMARY KEY,
    nome_doador VARCHAR(100),
    data_doacao DATE DEFAULT (CURRENT_DATE),
    fk_livro INT NOT NULL,
    FOREIGN KEY (fk_livro) REFERENCES LIVRO(id_livro)
);

-- 11. Table: USUARIO
CREATE TABLE IF NOT EXISTS USUARIO (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- ADMIN, OPERADOR
    data_ultima_senha DATE DEFAULT (CURRENT_DATE) -- Track password age
);

-- 12. Table: EVENTO (Coffee events, Book exchange)
CREATE TABLE IF NOT EXISTS EVENTO (
    id_evento INT AUTO_INCREMENT PRIMARY KEY,
    nome_evento VARCHAR(100) NOT NULL,
    data_evento DATETIME NOT NULL,
    tipo_evento VARCHAR(30), -- TROCA_LIVROS, WORKSHOP_CAFE
    descricao TEXT
);

-- 13. Table: FICHA_TECNICA (Many-to-Many Recipe between Products and Ingredients)
CREATE TABLE IF NOT EXISTS FICHA_TECNICA (
    id_ficha INT AUTO_INCREMENT PRIMARY KEY,
    fk_produto INT NOT NULL,
    fk_ingrediente INT NOT NULL,
    quantidade_necessaria DOUBLE NOT NULL,
    FOREIGN KEY (fk_produto) REFERENCES PRODUTO_CONSUMO(id_produto) ON DELETE CASCADE,
    FOREIGN KEY (fk_ingrediente) REFERENCES INGREDIENTE(id_ingrediente)
);

-- 14. Table: PARTICIPACAO_EVENTO (Many-to-Many attendance list between Events and Customers)
CREATE TABLE IF NOT EXISTS PARTICIPACAO_EVENTO (
    id_participacao INT AUTO_INCREMENT PRIMARY KEY,
    fk_evento INT NOT NULL,
    fk_cliente INT NOT NULL,
    data_inscricao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_evento) REFERENCES EVENTO(id_evento) ON DELETE CASCADE,
    FOREIGN KEY (fk_cliente) REFERENCES CLIENTE(id_cliente) ON DELETE CASCADE
);

-- Seed Data: GENERO_LIVRO
INSERT INTO GENERO_LIVRO (nome_genero, localizacao_estante) VALUES 
('Ficção Científica', 'Estante A1 - Corredor Azul'),
('Suspense & Thriller', 'Estante B2 - Próximo ao Café'),
('Clássicos Literários', 'Estante C1 - Parede de Madeira'),
('Romance', 'Estante D1 - Proximo as poltronas'),
('Filosofia', 'Mezanino - Área de Silêncio'),
('Gastronomia', 'Prateleira Suspensa - Balcão');

-- Seed Data: EDITORA
INSERT INTO EDITORA (nome_editora, cidade) VALUES 
('Companhia das Letras', 'São Paulo'),
('Editora Record', 'Rio de Janeiro'),
('Intrínseca', 'Rio de Janeiro'),
('Aleph', 'São Paulo');

-- Seed Data: FORNECEDOR
INSERT INTO FORNECEDOR (nome_fantasia, cnpj, contato, tipo_produto) VALUES 
('Grãos do Brasil', '11.222.333/0001-00', '(11) 91111-2222', 'Café em Grãos'),
('Padaria Central', '44.555.666/0001-99', '(11) 93333-4444', 'Salgados e Doces'),
('Distribuidora de Copos Ltda', '55.666.777/0001-88', '(11) 94444-5555', 'Descartáveis');

-- Seed Data: CLIENTE
INSERT INTO CLIENTE (nome, cpf, email, telefone, pontos_fidelidade, data_nascimento) VALUES 
('Ana Beatriz Silva', '111.444.777-35', 'anabeatriz@email.com', '(11) 98765-4321', 120, '1995-05-15'),
('Carlos Henrique Souza', '123.456.789-02', 'carlos.henrique@email.com', '(11) 97654-3210', 50, '1988-05-20'),
('Mariana Costa Santos', '111.222.333-96', 'mariana.santos@email.com', '(21) 99888-7777', 240, '2000-03-10'),
('Rodrigo de Oliveira', '444.555.666-19', 'rodrigo.oliveira@email.com', '(31) 99123-4567', 80, '1992-09-25'),
('Juliana Mendes Abreu', '777.888.999-41', 'juliana.abreu@email.com', '(11) 98888-9999', 310, '1985-12-05');

-- Seed Data: LIVRO
INSERT INTO LIVRO (titulo, autor, condicao_livro, preco_venda, estoque_atual, fk_genero, fk_editora, image_path) VALUES 
('Duna', 'Frank Herbert', 'Novo', 89.90, 15, 1, 4, 'src/main/resources/assets/duna.jpg'),
('Fundação', 'Isaac Asimov', 'Usado (Excelente)', 45.00, 2, 1, 4, 'src/main/resources/assets/fundacao.jpg'),
('O Iluminado', 'Stephen King', 'Novo', 65.00, 8, 2, 3, 'src/main/resources/assets/iluminado.jpg'),
('Dom Casmurro', 'Machado de Assis', 'Usado (Marcas de Tempo)', 15.00, 0, 3, 1, 'src/main/resources/assets/domcasmurro.jpg'),
('A República', 'Platão', 'Novo', 42.00, 1, 4, 1, 'src/main/resources/assets/republica.jpg'),
('O Pequeno Príncipe', 'Antoine de Saint-Exupéry', 'Novo', 35.00, 20, 3, 2, 'src/main/resources/assets/pequenoprincipe.jpg'),
('1984', 'George Orwell', 'Novo', 49.90, 10, 1, 1, 'src/main/resources/assets/1984.jpg'),
('Cem Anos de Solidão', 'Gabriel García Márquez', 'Novo', 75.00, 5, 4, 2, 'src/main/resources/assets/cemanos.jpg'),
('Sherlock Holmes: Estudo em Vermelho', 'Arthur Conan Doyle', 'Usado (Excelente)', 25.00, 3, 2, 2, 'src/main/resources/assets/sherlock.jpg'),
('O Hobbit', 'J.R.R. Tolkien', 'Novo', 59.90, 8, 1, 3, 'src/main/resources/assets/hobbit.jpg'),
('O Cortiço', 'Aluísio Azevedo', 'Usado (Marcas de Tempo)', 12.50, 4, 3, 1, 'src/main/resources/assets/cortico.jpg');

-- Seed Data: PRODUTO_CONSUMO
INSERT INTO PRODUTO_CONSUMO (nome_alimento, preco_unitario, categoria_cardapio, fk_fornecedor, image_path) VALUES 
('Shake Shake Shakespeare (Mocha)', 14.50, 'Bebidas Quentes', 1, 'src/main/resources/assets/mocha.jpg'),
('Capuccino Hemingway', 12.00, 'Bebidas Quentes', 1, 'src/main/resources/assets/capuccino.jpg'),
('Pão de Queijo da Vila', 5.50, 'Salgados', 2, 'src/main/resources/assets/pao_queijo.jpg'),
('Torta Red Velvet "Duna"', 18.00, 'Doces', 2, 'src/main/resources/assets/torta_duna.jpg'),
('Suco Natural "Laranja Mecânica"', 9.50, 'Bebidas Frias', 2, 'src/main/resources/assets/suco_laranja.jpg'),
('Sanduíche "Metamorfose"', 22.00, 'Salgados', 2, 'src/main/resources/assets/sanduiche.jpg'),
('Croissant Baudelaire', 9.50, 'Salgados', 2, 'src/main/resources/assets/croissant.jpg'),
('Empada Edgar Allan Poe', 8.00, 'Salgados', 2, 'src/main/resources/assets/empada.jpg'),
('Cold Brew Orwell', 11.50, 'Bebidas Frias', 1, 'src/main/resources/assets/cold_brew.jpg'),
('Soda Italiana Poe', 10.00, 'Bebidas Frias', 2, 'src/main/resources/assets/soda_italiana.jpg'),
('Torta de Limão Virginia Woolf', 15.00, 'Doces', 2, 'src/main/resources/assets/torta_limao.jpg'),
('Brownie Bukowski', 12.00, 'Doces', 2, 'src/main/resources/assets/brownie.jpg');

-- Seed Data: INGREDIENTE
INSERT INTO INGREDIENTE (nome_ingrediente, quantidade_atual, unidade_medida, fk_fornecedor) VALUES 
('Grãos de Café (Espresso)', 5000.0, 'g', 1), 
('Leite Integral', 10000.0, 'ml', 2), 
('Chocolate em Pó', 3000.0, 'g', 2), 
('Copos Descartáveis', 500.0, 'un', 3), 
('Canela em Pó', 1000.0, 'g', 2), 
('Essência de Baunilha', 500.0, 'ml', 2), 
('Chá Verde Folhas', 1000.0, 'g', 1), 
('Limão (Suco)', 2000.0, 'ml', 2);

-- Seed Data: FICHA_TECNICA
INSERT INTO FICHA_TECNICA (fk_produto, fk_ingrediente, quantidade_necessaria) VALUES
(1, 1, 15.0),  -- Mocha usa 15g de Grãos de Café
(1, 2, 150.0), -- Mocha usa 150ml de Leite Integral
(1, 3, 30.0),  -- Mocha usa 30g de Chocolate em Pó
(1, 4, 1.0),   -- Mocha usa 1 Copo Descartável
(2, 1, 15.0),  -- Capuccino usa 15g de Grãos de Café
(2, 2, 200.0), -- Capuccino usa 200ml de Leite Integral
(2, 4, 1.0);   -- Capuccino usa 1 Copo Descartável

-- Seed Data: USUARIO
INSERT INTO USUARIO (username, password, role, data_ultima_senha) VALUES 
('admin', 'admin123', 'ADMIN', '2026-01-01'), -- Expired for testing
('operador', 'op123', 'OPERADOR', CURRENT_DATE);

-- Seed Data: EVENTO
INSERT INTO EVENTO (nome_evento, data_evento, tipo_evento, descricao) VALUES 
('Clube do Livro: Ficção Científica', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 DAY), 'TROCA_LIVROS', 'Discussão sobre o clássico ''Duna'' de Frank Herbert, com troca de livros entre os participantes e café cortesia.'),
('Workshop: Arte do Espresso e Latte Art', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 12 DAY), 'WORKSHOP_CAFE', 'Aprenda a tirar o espresso perfeito e a criar desenhos incríveis com o leite vaporizado. Inclui degustação.'),
('Noite de Poesia e Sarau', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 20 DAY), 'OUTROS', 'Espaço aberto para leitura de poesias e apresentações acústicas no mezanino do Coffee&Books.'),
('Palestra: Realismo na Literatura Brasileira', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 15 DAY), 'OUTROS', 'Um bate-papo descontraído sobre realismo na literatura brasileira com encenação teatral.'),
('Oficina de Escrita Criativa: Mistérios de Poe', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 8 DAY), 'OUTROS', 'Aprenda técnicas de suspense e mistério na prosa curta de ficção gótica de Edgar Allan Poe.'),
('Lançamento: Sherlock & Watson', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 4 DAY), 'OUTROS', 'Lançamento da nova edição de colecionador de ''O Cão dos Baskerville'' com sessão de autógrafos.'),
('Banquete de Hogwarts: Doces e Literatura', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 6 DAY), 'OUTROS', 'Degustação inspirada nas comidas do universo mágico de Harry Potter com trivia literária.'),
('Encontro Tolkien: Terra Média e Mitologia', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 10 DAY), 'OUTROS', 'Grupo de estudos sobre as línguas élficas e a construção de mundos de J.R.R. Tolkien.'),
('Café Filosófico: A República de Platão', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 22 DAY), 'OUTROS', 'Debate mediado sobre a alegoria da caverna e a busca pela verdade no mezanino.'),
('Clube de Leitura: Virginia Woolf', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 18 DAY), 'OUTROS', 'Análise e leitura compartilhada do livro ''Ao Farol'' com café coado e bolinhos.');

-- Seed Data: PARTICIPACAO_EVENTO
INSERT INTO PARTICIPACAO_EVENTO (fk_evento, fk_cliente) VALUES
(1, 1),
(1, 2),
(2, 3),
(2, 4),
(3, 1),
(3, 5);

-- Seed Data: VENDA_CONSOLIDADA & ITEM_VENDA_GERAL
-- populated dynamically by DatabaseUtil
