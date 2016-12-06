# Simulador de Cache

O simulador de cache possui três funções: "write", "read" e "show". A representação usada para a memória pode ser vista abaixo.

EXEMPLO:

------------------

::: Memória Principal :::
Conteúdos da memória: [<><><><>][] [] [] [] [] [] [] [] [] [] [] [] [] [] [] 

::: Memória cache::: 
Conteúdos da cache: ([<0><><><>])()()()

Posição na cache :   0 1 2 3 
Blocos nas linhas:   0 n n n 

Hits: 0
Misses: 1

-----------------

Definições:

<> é a representação para uma WORD
[] é a representação para um BLOCO
() é a representação para uma LINHA

Quando não há nada dentro, é porque está vazio.

([<0><><><>]) significa que há uma linha da cache com um bloco da memória principal e uma WORD com o valor 0. As outras estão nulas.

"Posição na cache" é um contador de índices das linhas da cache.
"Blocos nas linhas" diz quais blocos estão em quais linhas da cache.

EXEMPLO:

-----------------

Posição na cache:   0 1 2 3 
Blocos nas linhas:  0 5 1 4 

-----------------

Essa representação quer dizer que o bloco 0 da memória principal está na linha 0 da cache, o bloco 5 da memória está na linha 1 da cache, o bloco 1 da memória está na linha 2 da cache e o bloco 4 da memória está na linha 3 da cache.
