module Main where

import Test.QuickCheck
import System.IO
import Data.List (intercalate)

-- 1. Definição dos Tipos de Dados
data PlanType = Free | PremiumBase | PremiumTop deriving (Show, Enum, Bounded)
data MusicType = Normal | Explicit | Multimedia deriving Show

-- 2. Geradores de Primitivos
genEmail :: Gen String
genEmail = do
    user <- listOf1 $ elements (['a'..'z'] ++ ['0'..'9'])
    dom  <- elements ["gmail.com", "outlook.pt", "uminho.pt"]
    return $ user ++ "@" ++ dom

genName :: Gen String
genName = elements ["Ana", "Bruno", "Catarina", "Diogo", "Elena", "Fernando", "Gonçalo"]

-- 3. Instâncias Arbitrary
instance Arbitrary PlanType where
    arbitrary = frequency [(60, return Free), (30, return PremiumBase), (10, return PremiumTop)]

instance Arbitrary MusicType where
    arbitrary = frequency [(70, return Normal), (20, return Explicit), (10, return Multimedia)]

genMusicCSV :: Gen String
genMusicCSV = do
    nome    <- elements ["Bohemian Rhapsody", "Stairway to Heaven", "Tempo", "Sol"]
    artista <- elements ["Queen", "Led Zeppelin", "Pedro Abrunhosa"]
    mType   <- arbitrary :: Gen MusicType
    duracao <- choose (30, 600) :: Gen Int
    let isExpl = case mType of Explicit -> "true"; _ -> "false"
    let url    = case mType of Multimedia -> "http://youtube.com/watch?v=xyz"; _ -> "null"
    return $ intercalate "|" [nome, artista, show duracao, isExpl, url]

genUserCSV :: Gen String
genUserCSV = do
    nome    <- genName
    userTag <- choose (1000, 9999) :: Gen Int
    let uniqueNome = nome ++ show userTag
    let email = uniqueNome ++ "@uminho.pt"
    pass    <- vectorOf 8 $ elements (['a'..'z'] ++ ['0'..'9'])
    plano   <- arbitrary :: Gen PlanType
    return $ intercalate "|" [uniqueNome, email, pass, show plano]

genAlbumCSV :: Gen String
genAlbumCSV = do
    titulo  <- elements ["Greatest Hits", "Live in Porto", "Dark Side"]
    artista <- elements ["Queen", "Daft Punk", "Pink Floyd"]
    numMusicas <- choose (5, 12) :: Gen Int
    musicas <- vectorOf numMusicas (elements ["SongA", "SongB", "SongC"])
    return $ titulo ++ "|" ++ artista ++ "|" ++ intercalate ";" musicas

genPlaylistCSV :: Gen String
genPlaylistCSV = do
    nome    <- elements ["Party Mix", "Focus", "Gym", "Relax"]
    criador <- genEmail
    numMusicas <- choose (3, 20) :: Gen Int
    musicas <- vectorOf numMusicas (elements ["Song1", "Song2", "Song3"])
    publica <- arbitrary :: Gen Bool
    return $ intercalate "|" [nome, criador, show publica, intercalate ";" musicas]

genPlanTestCSV :: Gen String
genPlanTestCSV = do
    email       <- genEmail
    planoInicial <- arbitrary :: Gen PlanType
    numReproducoes <- choose (1, 50) :: Gen Int
    return $ intercalate "|" [email, show planoInicial, show numReproducoes]

genStatisticsScenario :: Gen String
genStatisticsScenario = do
    users   <- vectorOf 5 (elements ["UserA", "UserB", "UserC"])
    musics  <- vectorOf 5 (elements ["Song1", "Song2", "Song3"])
    reproducoes <- vectorOf 100 $ do
        u <- elements users
        m <- elements musics
        return (u ++ ":" ++ m)
    return $ intercalate ";" (reproducoes :: [String])

-- Função Principal
main :: IO()
main = do
    musicas   <- generate (vectorOf 500 genMusicCSV)
    users     <- generate (vectorOf 200 genUserCSV)
    albuns    <- generate (vectorOf 50 genAlbumCSV)
    playlists <- generate (vectorOf 100 genPlaylistCSV)
    plans     <- generate (vectorOf 200 genPlanTestCSV)
    stats     <- generate (vectorOf 50 genStatisticsScenario)
 
    -- Escrita dos ficheiros nos recursos de teste do Maven
    writeFile "src/quickcheckTests/csv/test_plans.csv" (unlines plans)
    writeFile "src/quickcheckTests/csv/test_stats.csv" (unlines stats)
    writeFile "src/quickcheckTests/csv/test_musics.csv" (unlines musicas)
    writeFile "src/quickcheckTests/csv/test_users.csv" (unlines users)
    writeFile "src/quickcheckTests/csv/test_albums.csv" (unlines albuns)
    writeFile "src/quickcheckTests/csv/test_playlists.csv" (unlines playlists)
    
    putStrLn "✅ Todos os dados de larga escala gerados com sucesso!"