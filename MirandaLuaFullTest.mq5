//+------------------------------------------------------------------+
//|                                                   MirandaLua.mq5 |
//|                                                 Allan de Miranda |
//|                               https://github.com/allandemiranda/ |
//+------------------------------------------------------------------+
#property copyright "Allan de Miranda"
#property link      "https://github.com/allandemiranda/"
#property version   "1.20"

#include <Trade\Trade.mqh>  // Para a classe CTrade
#include <Trade\PositionInfo.mqh>  // Para a classe CPositionInfo
#include <WinINet.mqh>

CTrade trade;           // Objeto para gerenciar as operações
CPositionInfo position; // Objeto para acessar informações das posições

//+------------------------------------------------------------------+
//| Definição de ENUMs                                               |
//+------------------------------------------------------------------+
enum ENUM_ALLOWED_TIMEFRAMES {
   M15 = PERIOD_M15,
   M30 = PERIOD_M30,
   H1 = PERIOD_H1
};

enum ENUM_TIME_INTERVALS {
   Interval_00_04,
   Interval_04_08,
   Interval_08_12,
   Interval_12_16,
   Interval_16_20,
   Interval_20_24
};

enum ENUM_SIGNAL_INDICATOR {
   BUY,
   SELL,
   NEUTRAL,
   DISABLE
};

//+------------------------------------------------------------------+
//| Inputs do robô                                                   |
//+------------------------------------------------------------------+

// OpenAI
input string apiKey = "API_KEY"; // Sua chave API do OpenAI
input string modelOpenAI = "gpt-4o";
input int numberOfCandlesOpenAI = 100; // Number of candles to process OpenAI

// Tamanho do lote
input double lotSize = 0.01; // Tamanho do lote para negociação

// Timeframes activated
input bool m15activate = true;
input bool m30activate = true;
input bool h1activate = true;

// Parâmetros do ADX
input int adxPeriod = 14;
input double adxLevel = 25;

// Parâmetros do RSI
input int rsiPeriod = 14;
input double rsiOverbought = 70;
input double rsiOversold = 30;
input ENUM_APPLIED_PRICE rsiAppliedPrice = PRICE_CLOSE;

// Parâmetros do MACD
input int macdFastPeriod = 12;
input int macdSlowperiod = 26;
input int macdSignalPeriod = 9;
input ENUM_APPLIED_PRICE macdAppliedPrice = PRICE_CLOSE;

// Parâmetros do Stochastic
input int stochasticKperiod = 5;
input int stochasticDperiod = 3;
input int stochasticSlowing = 3;
input ENUM_MA_METHOD stochasticMaMethod = MODE_SMA;
input ENUM_STO_PRICE stochasticPriceField = STO_LOWHIGH;
input double stochasticOverbought = 80;
input double stochasticOversold = 20;

// Parâmetros do Williams' Percent Range
input int percentRangePeriod = 14;
input double percentRangeOversold = -80;
input double percentRangeOverbought = -20;

// Parâmetros do Commodity Channel Index
input int cciPeriod = 14;
input ENUM_APPLIED_PRICE cciAppliedPrice = PRICE_TYPICAL;
input double cciOversold = -100;
input double cciOverbought = 100;

// Parâmetros do Relative Vigor Index
input int rviPeriod = 10;

//+------------------------------------------------------------------+
//| Variageis globais                                                |
//+------------------------------------------------------------------+

// Currency pair
string symbolName = Symbol();
double symbolPoint = _Point;

// Variável para armazenar o manipulador dos indicatores
int rsiHandleM15, adxHandleM15, acHandleM15, macdHandleM15, stochasticHandleM15, percentRangeHandleM15, cciHandleM15, rviHandleM15, rsiHandleM30, adxHandleM30, acHandleM30, macdHandleM30, stochasticHandleM30, percentRangeHandleM30, cciHandleM30, rviHandleM30, rsiHandleH1, adxHandleH1, acHandleH1, macdHandleH1, stochasticHandleH1, percentRangeHandleH1, cciHandleH1, rviHandleH1;

// Variável para armazenar o timestamp do último candlestick criado
datetime cacheLastCandleTimeM15 = 0;
datetime cacheLastCandleTimeM30 = 0;
datetime cacheLastCandleTimeH1 = 0;

//+------------------------------------------------------------------+
//| Função OnInit                                                    |
//+------------------------------------------------------------------+
int OnInit(void) {

   adxHandleM15 = iADX(symbolName, PERIOD_M15, adxPeriod);
   if(adxHandleM15 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iADX, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   rsiHandleM15 = iRSI(symbolName, PERIOD_M15, rsiPeriod, rsiAppliedPrice);
   if(rsiHandleM15 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iRSI, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   acHandleM15 = iAC(symbolName, PERIOD_M15);
   if(acHandleM15 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iAC, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   macdHandleM15 = iMACD(symbolName, PERIOD_M15, macdFastPeriod, macdSlowperiod, macdSignalPeriod, macdAppliedPrice);
   if(macdHandleM15 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iMACD, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   stochasticHandleM15 = iStochastic(symbolName, PERIOD_M15, stochasticKperiod, stochasticDperiod, stochasticSlowing, stochasticMaMethod, stochasticPriceField);
   if(stochasticHandleM15 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iStochastic, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   percentRangeHandleM15 = iWPR(symbolName, PERIOD_M15, percentRangePeriod);
   if(percentRangeHandleM15 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iWPR, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   cciHandleM15 = iCCI(symbolName, PERIOD_M15, cciPeriod, cciAppliedPrice);
   if(cciHandleM15 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iCCI, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   rviHandleM15 = iRVI(symbolName, PERIOD_M15, rviPeriod);
   if(rviHandleM15 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iRVI, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   adxHandleM30 = iADX(symbolName, PERIOD_M30, adxPeriod);
   if(adxHandleM30 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iADX, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   rsiHandleM30 = iRSI(symbolName, PERIOD_M30, rsiPeriod, rsiAppliedPrice);
   if(rsiHandleM30 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iRSI, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   acHandleM30 = iAC(symbolName, PERIOD_M30);
   if(acHandleM30 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iAC, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   macdHandleM30 = iMACD(symbolName, PERIOD_M30, macdFastPeriod, macdSlowperiod, macdSignalPeriod, macdAppliedPrice);
   if(macdHandleM30 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iMACD, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   stochasticHandleM30 = iStochastic(symbolName, PERIOD_M30, stochasticKperiod, stochasticDperiod, stochasticSlowing, stochasticMaMethod, stochasticPriceField);
   if(stochasticHandleM30 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iStochastic, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   percentRangeHandleM30 = iWPR(symbolName, PERIOD_M30, percentRangePeriod);
   if(percentRangeHandleM30 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iWPR, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   cciHandleM30 = iCCI(symbolName, PERIOD_M30, cciPeriod, cciAppliedPrice);
   if(cciHandleM30 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iCCI, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   rviHandleM30 = iRVI(symbolName, PERIOD_M30, rviPeriod);
   if(rviHandleM30 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iRVI, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   adxHandleH1 = iADX(symbolName, PERIOD_H1, adxPeriod);
   if(adxHandleH1 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iADX, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   rsiHandleH1 = iRSI(symbolName, PERIOD_H1, rsiPeriod, rsiAppliedPrice);
   if(rsiHandleH1 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iRSI, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   acHandleH1 = iAC(symbolName, PERIOD_H1);
   if(acHandleH1 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iAC, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   macdHandleH1 = iMACD(symbolName, PERIOD_H1, macdFastPeriod, macdSlowperiod, macdSignalPeriod, macdAppliedPrice);
   if(macdHandleH1 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iMACD, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   stochasticHandleH1 = iStochastic(symbolName, PERIOD_H1, stochasticKperiod, stochasticDperiod, stochasticSlowing, stochasticMaMethod, stochasticPriceField);
   if(stochasticHandleH1 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iStochastic, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   percentRangeHandleH1 = iWPR(symbolName, PERIOD_H1, percentRangePeriod);
   if(percentRangeHandleH1 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iWPR, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   cciHandleH1 = iCCI(symbolName, PERIOD_H1, cciPeriod, cciAppliedPrice);
   if(cciHandleH1 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iCCI, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   rviHandleH1 = iRVI(symbolName, PERIOD_H1, rviPeriod);
   if(rviHandleH1 == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iRVI, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   if(!m15activate && !m30activate && !h1activate) {
      PrintFormat("Falha, todos os timesframes estão desativados!");
      return(INIT_FAILED);
   }

   return(INIT_SUCCEEDED);
}

//+------------------------------------------------------------------+
//| Função OnTick                                                    |
//+------------------------------------------------------------------+
int tmpOpenAII = 0;
void OnTick(void) {

   const int matrixData[][11] = {

      {15,1,0,0,0,0,0,0,1,0,0},
      {15,1,0,0,0,0,0,0,1,1,0},
      {15,1,2,0,0,0,1,0,0,0,1},
      {15,1,3,0,0,0,0,0,0,1,0},
      {15,1,3,0,0,0,0,0,1,1,0},
      {15,1,3,0,0,0,0,0,1,0,0},
      {15,1,4,0,0,1,0,0,0,0,1},
      {15,1,4,0,0,1,0,0,0,0,0},
      {15,1,4,0,0,0,1,0,0,0,1},
      {15,1,4,0,0,1,1,0,0,0,0},
      {15,2,2,1,0,0,1,0,0,0,0},
      {15,2,2,1,0,0,0,0,0,0,0},
      {15,2,2,0,0,0,1,0,0,0,1},
      {15,2,3,1,0,0,0,0,0,0,0},
      {15,2,3,0,0,0,0,0,1,0,0},
      {15,3,2,1,0,0,1,0,0,0,0},
      {15,3,4,0,0,0,1,0,0,0,1},
      {15,4,2,1,0,1,0,0,0,0,0},
      {15,4,2,1,0,1,1,0,0,0,0},
      {15,4,4,0,0,0,0,0,0,0,1},
      {15,4,4,0,0,0,0,0,1,0,1},
      {15,4,4,1,0,1,0,0,0,0,0},
      {15,4,4,1,0,1,1,0,0,0,0},
      {15,5,0,0,0,0,1,0,0,0,1},
      {15,5,0,0,0,0,0,0,0,1,0},
      {15,5,1,0,0,0,0,0,1,1,0},
      {15,5,1,0,0,0,0,0,0,1,0},
      {15,5,4,1,0,1,0,0,0,0,0},
      {15,5,5,0,0,0,0,0,1,1,0},
      {15,5,5,0,0,0,0,0,0,1,0},
      {30,1,0,0,0,0,0,0,1,0,0},
      {30,1,2,1,0,0,1,0,0,0,0},
      {30,1,2,1,0,0,0,0,0,0,0},
      {30,1,3,0,0,0,1,0,0,0,0},
      {30,1,4,0,0,0,0,0,0,0,1},
      {30,1,4,0,0,1,1,0,0,0,0},
      {30,1,4,0,0,1,0,0,0,0,0},
      {30,2,2,0,0,0,0,0,0,0,1},
      {30,2,2,0,0,0,1,0,0,0,0},
      {30,2,3,1,0,0,0,0,0,0,0},
      {30,2,4,0,0,1,0,0,0,0,0},
      {30,3,2,0,0,0,0,0,0,1,0},
      {30,3,2,0,0,0,0,0,1,0,0},
      {30,3,2,0,0,0,0,0,1,1,0},
      {30,3,4,0,0,0,0,0,0,1,0},
      {30,3,4,0,0,0,0,0,0,0,1},
      {30,3,4,0,0,0,0,0,1,0,0},
      {30,3,4,0,0,0,0,0,1,1,0},
      {30,4,2,0,0,1,0,0,0,0,0},
      {30,4,2,0,0,1,1,0,0,0,0},
      {30,4,2,0,0,0,0,0,0,0,1},
      {30,4,2,1,0,1,0,0,0,0,0},
      {30,4,3,0,0,1,0,0,0,0,0},
      {30,4,3,0,0,1,1,0,0,0,0},
      {30,4,4,0,0,1,1,0,0,0,0},
      {30,4,4,0,0,1,0,0,0,0,0},
      {30,4,4,1,0,1,1,0,0,0,0},
      {30,4,4,1,0,1,0,0,0,0,0},
      {30,5,0,0,0,0,0,0,1,0,0},
      {30,5,1,0,0,0,0,0,1,0,0},
      {30,5,3,0,0,0,0,0,1,1,0},
      {16385,1,3,0,0,0,1,0,0,0,0},
      {16385,1,3,0,0,1,0,0,0,0,0},
      {16385,1,3,0,0,1,1,0,0,0,0},
      {16385,1,4,0,0,0,0,0,0,1,0},
      {16385,2,1,1,0,0,0,0,0,0,0},
      {16385,2,2,0,0,0,1,0,0,0,0},
      {16385,2,2,0,0,1,0,0,0,0,0},
      {16385,2,3,0,0,0,0,0,0,1,0},
      {16385,2,3,0,0,1,0,0,0,0,0},
      {16385,4,0,0,0,0,1,0,0,0,0},
      {16385,4,4,0,0,1,0,0,0,0,0},
      {16385,5,3,0,0,0,0,0,0,1,0}

   };

   datetime lastCandleTimeM15 = iTime(symbolName, PERIOD_M15, 1);
   bool m15New = false;
   if(cacheLastCandleTimeM15 != lastCandleTimeM15) {
      cacheLastCandleTimeM15 = lastCandleTimeM15;
      m15New = true;
   }

   datetime lastCandleTimeM30 = iTime(symbolName, PERIOD_M30, 1);
   bool m30New = false;
   if(cacheLastCandleTimeM30 != lastCandleTimeM30) {
      cacheLastCandleTimeM30 = lastCandleTimeM30;
      m30New = true;
   }

   datetime lastCandleTimeH1 = iTime(symbolName, PERIOD_H1, 1);
   bool h1New = false;
   if(cacheLastCandleTimeH1 != lastCandleTimeH1) {
      cacheLastCandleTimeH1 = lastCandleTimeH1;
      h1New = true;
   }

   // {buy, sell}
   int m15Positinos[2] = {0, 0};
   int m30Positinos[2] = {0, 0};
   int h1Positinos[2] = {0, 0};

   for(int i=0;i<ArrayRange(matrixData, 0);i++) {

      ENUM_TIMEFRAMES period = (matrixData[i][0] == 15 ? PERIOD_M15 : (matrixData[i][0] == 30 ? PERIOD_M30 : PERIOD_H1));

      // Verifique se um novo candlestick foi formado (o tempo mudou)
      if((period == PERIOD_M15 && m15New && m15activate) || (period == PERIOD_M30 && m30New && m30activate) || (period == PERIOD_H1 && h1New && h1activate)) {

         if(IsTradingTimeAllowed(period == PERIOD_M15 ? lastCandleTimeM15 : (period == PERIOD_M30 ? lastCandleTimeM30 : lastCandleTimeH1), (ENUM_DAY_OF_WEEK) matrixData[i][1], (ENUM_TIME_INTERVALS) matrixData[i][2])) {

            ENUM_SIGNAL_INDICATOR signals[] = {matrixData[i][3] == 0 ? DISABLE : getAdxSignal(period == PERIOD_M15 ? adxHandleM15 : (period == PERIOD_M30 ? adxHandleM30 : adxHandleH1)),
                                               matrixData[i][4] == 0 ? DISABLE : getRsiSignal(period == PERIOD_M15 ? rsiHandleM15 : (period == PERIOD_M30 ? rsiHandleM30 : rsiHandleH1)),
                                               matrixData[i][5] == 0 ? DISABLE : getAcSignal(period == PERIOD_M15 ? acHandleM15 : (period == PERIOD_M30 ? acHandleM30 : acHandleH1)),
                                               matrixData[i][6] == 0 ? DISABLE : getMacdSignal(period == PERIOD_M15 ? macdHandleM15 : (period == PERIOD_M30 ? macdHandleM30 : macdHandleH1)),
                                               matrixData[i][7] == 0 ? DISABLE : getStochasticSignal(period == PERIOD_M15 ? stochasticHandleM15 : (period == PERIOD_M30 ? stochasticHandleM30 : stochasticHandleH1)),
                                               matrixData[i][8] == 0 ? DISABLE : getWPRSignal(period == PERIOD_M15 ? percentRangeHandleM15 : (period == PERIOD_M30 ? percentRangeHandleM30 : percentRangeHandleH1)),
                                               matrixData[i][9] == 0 ? DISABLE : getCCISignal(period == PERIOD_M15 ? cciHandleM15 : (period == PERIOD_M30 ? cciHandleM30 : cciHandleH1)),
                                               matrixData[i][10] == 0 ? DISABLE : getRVISignal(period == PERIOD_M15 ? rviHandleM15 : (period == PERIOD_M30 ? rviHandleM30 : rviHandleH1))
                                              };

            int neutralSignal = 0;
            int buySignal = 0;
            int sellSignal = 0;

            for(int i = 0; i < ArraySize(signals); i++) {
               switch(signals[i]) {
               case BUY:
                  buySignal++;
                  break;
               case SELL:
                  sellSignal++;
                  break;
               case NEUTRAL:
                  neutralSignal++;
                  break;
               }
            }

            int totalSignal = neutralSignal + buySignal + sellSignal;

            if(neutralSignal == 0) {
               if(totalSignal == buySignal) {
                  if(period == PERIOD_M15) {
                     m15Positinos[0]++;
                  } else if(period == PERIOD_M30) {
                     m30Positinos[0]++;
                  } else if(period == PERIOD_H1) {
                     h1Positinos[0]++;
                  }
               } else if(totalSignal == sellSignal) {
                  if(period == PERIOD_M15) {
                     m15Positinos[1]++;
                  } else if(period == PERIOD_M30) {
                     m30Positinos[1]++;
                  } else if(period == PERIOD_H1) {
                     h1Positinos[1]++;
                  }
               }
            }
         }
      }
   }

   // OPEN FOR M15
   if((m15Positinos[0] > 0 && m15Positinos[1] == 0) || (m15Positinos[1] > 0 && m15Positinos[0] == 0)) {
      OpenAIDecision(PERIOD_M15);
   }

   // OPEN FOR M30
   if((m30Positinos[0] > 0 && m30Positinos[1] == 0) || (m30Positinos[1] > 0 && m30Positinos[0] == 0)) {
      OpenAIDecision(PERIOD_M30);
   }

   // OPEN FOR H1
   if((h1Positinos[0] > 0 && h1Positinos[1] == 0) || (h1Positinos[1] > 0 && h1Positinos[0] == 0)) {
      OpenAIDecision(PERIOD_H1);
   }
}

//+------------------------------------------------------------------+
//| Função para coletar dados dos últimos candles                    |
//+------------------------------------------------------------------+
string GetCandleData(const ENUM_TIMEFRAMES timeframe) {
   string candleData = "";

   // Loop para obter dados dos últimos candles
   for (int i = 1; i <= numberOfCandlesOpenAI; i++) {
      double open = iOpen(symbolName, timeframe, i);
      double close = iClose(symbolName, timeframe, i);
      double high = iHigh(symbolName, timeframe, i);
      double low = iLow(symbolName, timeframe, i);
      long volume = iVolume(symbolName, timeframe, i);

      candleData += StringFormat("Candle %d: Open=%.5f, Close=%.5f, High=%.5f, Low=%.5f, Volume=%d\\n", i, open, close, high, low, volume);
   }

   return candleData;
}

void PrintMessageContent(const string jsonResponse) {
    // Define as strings de início e fim para localizar o conteúdo desejado
    string startPattern = "\"content\": \"";
    string endPattern = "\"";

    // Localiza a posição inicial do conteúdo de "content"
    int startPos = StringFind(jsonResponse, startPattern);
    if (startPos == -1) {
        Print("Erro: 'content' não encontrado no JSON.");
        return;
    }

    // Ajusta a posição para depois do startPattern
    startPos += StringLen(startPattern);

    // Localiza a posição de fechamento do conteúdo de "content"
    int endPos = StringFind(jsonResponse, endPattern, startPos);
    if (endPos == -1) {
        Print("Erro: Fim de 'content' não encontrado no JSON.");
        return;
    }

    // Extrai e imprime o conteúdo de "content"
    string messageContent = StringSubstr(jsonResponse, startPos, endPos - startPos);
    Print("Conteúdo de message.content: ", messageContent);
}

void AppendToFile(const string fileName, const string content) {
    // Abre o arquivo para leitura e escrita, criando-o se ele não existir
    int fileHandle = FileOpen(fileName, FILE_READ | FILE_WRITE | FILE_TXT | FILE_ANSI);

    // Verifica se o arquivo foi aberto com sucesso
    if (fileHandle == INVALID_HANDLE) {
        Print("Erro ao abrir o arquivo para escrita: ", GetLastError());
        return;
    }

    // Move o ponteiro de escrita para o final do arquivo para preservar o conteúdo existente
    FileSeek(fileHandle, 0, SEEK_END);

    // Escreve o conteúdo no arquivo com uma nova linha para separar as entradas de log
    FileWriteString(fileHandle, content + "\n");

    // Fecha o arquivo após a escrita
    FileClose(fileHandle);
    
    // Obter o caminho completo da pasta de dados do MetaTrader e o caminho completo do arquivo
    string fullPath = TerminalInfoString(TERMINAL_DATA_PATH) + "\\MQL5\\Files\\" + fileName;

    // Imprime o caminho completo do arquivo
    Print("Conteúdo adicionado ao arquivo: ", fullPath);
}

// Função para enviar consulta para OpenAI com dados de candles
void OpenAIDecision(const ENUM_TIMEFRAMES timeframe) { 
   // Construir a pergunta para a OpenAI
   string partsTF[];
   StringSplit(EnumToString(timeframe), '_', partsTF);

   string roleSystem = "{\"role\":\"system\",\"content\":\"You are providing a Forex market analysis for a " + symbolName + " currency pair and timeframe " + partsTF[1] +". You will receive the latest " + IntegerToString(numberOfCandlesOpenAI) + " candles, including Open price, Close price, High price, Low price, and Volume. Based on this data and in your data forex past information, combine Fibonacci Retracement with Support/Resistance, Candlestick patterns, Trading Trend Patterns, trend indicators, oscillators indicators, and Bill Williams indicators. Your response should recommend whether to open a buy or a sell position, including suggested Take Profit (TP) and Stop Loss (SL) levels, or to refrain from opening a position if conditions are not good, after your best data analysis. Provide only the decision and values, without additional explanation.\"}";
   
   string prompt = "Pair: " + symbolName + "\\nTimeframe: " + partsTF[1] + "\\nList of candles from newest to oldest:\\n" + GetCandleData(timeframe) + "Based on the provided data, determine if I should open a buy or sell position, including TP and SL, or if no action should be taken. Respond with: 'Order: [buy/sell/not], TP: [price], SL: [price]'";
   string roleUser = "{\"role\":\"user\", \"content\": \"" + prompt + "\"}";
   // Dados para a requisição
   string postData = "{\"model\":\"" + modelOpenAI + "\",\"max_tokens\":20,\"temperature\": 0.2,\"top_p\": 1,\"frequency_penalty\": 0,\"presence_penalty\": 0,\"messages\":[" + roleSystem + "," + roleUser + "]}";
   //AppendToFile("postData.json", postData);

   // Converter string postData para um array de char
   char postDataArray[];
   StringToCharArray(postData, postDataArray);

   char result[];           // Array para armazenar a resposta do servidor
   string result_headers;    // Para armazenar os cabeçalhos da resposta

   // Definir cabeçalhos (como string formatado)
   string headers = "Content-Type: application/json\r\nAuthorization: Bearer " + apiKey;

   // Chamada da função WebRequest, agora com parâmetros corretos
   int timeout = 10000; // 10 segundos de timeout
   //int res = WebRequest("POST", "https://api.openai.com/v1/chat/completions", headers, timeout, postDataArray, result, result_headers);
   int res = WebReq("POST", "api.openai.com", "/v1/chat/completions", 443, true, headers, postDataArray, result, result_headers);

   if (res != 200) {
      Print("Erro na requisição para OpenAI: ", GetLastError(), " res=", res);
      return;
   }

   string jsonResponse = CharArrayToString(result);
   PrintMessageContent(jsonResponse);
   // Processar a resposta JSON e obter a decisão de compra/venda
   ParseOpenAIResponse(jsonResponse, timeframe);
}

// Função para processar a resposta da OpenAI
void ParseOpenAIResponse(const string jsonResponse, const ENUM_TIMEFRAMES timeframe) {
   // Identifica o tipo de ordem (compra, venda ou nenhuma ação)
   ENUM_ORDER_TYPE orderType;
   if (StringFind(jsonResponse, "buy") >= 0) {
      orderType = ORDER_TYPE_BUY;
   } else if (StringFind(jsonResponse, "sell") >= 0) {
      orderType = ORDER_TYPE_SELL;
   } else {
      //Print("OpenAI decidiu não abrir uma operação ", symbolName, "/", EnumToString(timeframe));
      return;
   }

   // Inicializa Take Profit e Stop Loss com valores padrão
   double tp = 0.0, sl = 0.0;

   // Extração de TP
   int tpStart = StringFind(jsonResponse, "TP:") + 3;
   if (tpStart > 2) { // Verifica se TP: foi encontrado
      int tpEnd = StringFind(jsonResponse, ",", tpStart);
      if (tpEnd > tpStart) {
         tp = StringToDouble(StringSubstr(jsonResponse, tpStart, tpEnd - tpStart));
      }
   }

   // Extração de SL
   int slStart = StringFind(jsonResponse, "SL:") + 3;
   if (slStart > 2) { // Verifica se SL: foi encontrado
      int slEnd = StringFind(jsonResponse, "\"", slStart); // Procura a próxima aspas após o valor de SL
      if (slEnd < 0) {
         slEnd = StringLen(jsonResponse); // Se não encontrar aspas, vai até o final da string
      }
      sl = StringToDouble(StringSubstr(jsonResponse, slStart, slEnd - slStart));
   }

   // Chama a função para abrir a negociação com os valores extraídos
   if(tp > 0.0 && sl > 0.0) {
      OpenTrade(orderType, tp, sl, timeframe);
   } else {
      Print("OpenAI retornou mal TP ou SL. TP=", tp, ", SL=", sl, ", jsonResponse=", jsonResponse);
   }
}

//+------------------------------------------------------------------+
//| Função para obter o sinal do indicador ADX                       |
//+------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getAdxSignal(const int adxHandle) {

   double adxValue[1], plusDIValue[1], minusDIValue[1];
   if(CopyBuffer(adxHandle, 0, 1, 1, adxValue) > 0 &&  // ADX buffer
         CopyBuffer(adxHandle, 1, 1, 1, plusDIValue) > 0 &&  // +DI buffer
         CopyBuffer(adxHandle, 2, 1, 1, minusDIValue) > 0) { // -DI buffer

      if(adxValue[0] >= adxLevel) {
         if(plusDIValue[0] > minusDIValue[0]) {
            // BUY
            return BUY;
         } else if(plusDIValue[0] < minusDIValue[0]) {
            // SELL
            return SELL;
         }
      }

   } else {
      Print("Failed to retrieve ADX values.");
   }
   return NEUTRAL;
}

//+------------------------------------------------------------------+
//| Função para obter o sinal do indicador RSI                       |
//+------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getRsiSignal(const int rsiHandle) {

   double rsiValue[1];
   if(CopyBuffer(rsiHandle, 0, 1, 1, rsiValue) > 0) {

      if(rsiValue[0] > rsiOverbought) {
         // SELL
         return SELL;
      } else if(rsiValue[0] < rsiOversold) {
         // BUY
         return BUY;
      }

   } else {
      Print("Failed to retrieve RSI value.");
   }
   return NEUTRAL;
}

//+-------------------------------------------------------------------+
//| Função para obter o sinal do indicador Accelerator Oscillator (AC)|
//+-------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getAcSignal(const int acHandle) {

   double acValue[2];
   if(CopyBuffer(acHandle, 0, 1, 2, acValue) > 0) { // Fetch 2 bars (current and previous)

      // Check for AC signal based on the last 2 bars
      if(acValue[0] > 0 && acValue[0] < acValue[1]) {
         // Both bars are positive, indicating a potential BUY signal
         return BUY;
      } else if(acValue[0] < 0 && acValue[0] > acValue[1]) {
         // Both bars are negative, indicating a potential SELL signal
         return SELL;
      }
   } else {
      Print("Failed to retrieve AC values.");
   }
   return NEUTRAL; // If bars are mixed or an error occurs, return NEUTRAL
}

//+------------------------------------------------------------------+
//| Function to get MACD signal                                       |
//+------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getMacdSignal(const int macdHandle) {

   double macdLine[1], signalLine[1];  // Arrays to store the current and previous bar values
   // CopyBuffer for MACD line and Signal line to get the last 2 values (current and previous bars)
   if(CopyBuffer(macdHandle, 0, 1, 1, macdLine) > 0 &&   // MACD Line buffer
         CopyBuffer(macdHandle, 1, 1, 1, signalLine) > 0) { // Signal Line buffer

      // Check for MACD crossovers
      if(macdLine[0] > signalLine[0]) {
         //BUY;  // MACD line crossed above Signal line (bullish crossover)
         return BUY;
      } else if(macdLine[0] < signalLine[0]) {
         //SELL; // MACD line crossed below Signal line (bearish crossover)
         return SELL;
      }

   } else {
      Print("Failed to retrieve MACD values.");
   }
   return NEUTRAL; // If no crossover or an error occurs, return NEUTRAL
}

//+------------------------------------------------------------------+
//| Function to get Stochastic signal                                 |
//+------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getStochasticSignal(const int stochasticHandle) {

   double kLine[1], dLine[1];  // Arrays to store the current and previous bar values

   // CopyBuffer for Stochastic %K and %D lines to get the last 2 values (current and previous bars)
   if(CopyBuffer(stochasticHandle, 0, 1, 1, kLine) > 0 &&   // %K Line buffer
         CopyBuffer(stochasticHandle, 1, 1, 1, dLine) > 0) {   // %D Line buffer

      // Check for overbought/oversold conditions and crossovers
      if(kLine[0] < stochasticOversold && dLine[0] < stochasticOversold && kLine[0] >= dLine[0]) {  // Both in oversold region
         //BUY;  // %K line crosses above %D line, indicating a BUY signal
         return BUY;
      } else if(kLine[0] > stochasticOverbought && dLine[0] > stochasticOverbought && kLine[0] <= dLine[0]) { // Both in overbought region
         //SELL; // %K line crosses below %D line, indicating a SELL signal
         return SELL;
      }
   } else {
      Print("Failed to retrieve Stochastic values.");
   }

   return NEUTRAL;  // If no crossover or error occurs, return NEUTRAL
}

//+------------------------------------------------------------------+
//| Function to get Williams' Percent Range (%R) signal               |
//+------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getWPRSignal(const int percentRangeHandle) {

   double wprValue[1];  // Array to store the current value of the %R

   // CopyBuffer for %R to get the current bar value
   if(CopyBuffer(percentRangeHandle, 0, 1, 1, wprValue) > 0) {   // %R buffer

      // Check for overbought/oversold conditions based on %R value
      if(wprValue[0] < percentRangeOversold) {
         //BUY;  // %R is below -80, indicating a BUY signal (oversold)
         return BUY;
      } else if(wprValue[0] > percentRangeOverbought) {
         //SELL; // %R is above -20, indicating a SELL signal (overbought)
         return SELL;
      }
   } else {
      Print("Failed to retrieve %R values.");
   }

   return NEUTRAL;  // If no signal or an error occurs, return NEUTRAL
}

//+------------------------------------------------------------------+
//| Function to get Commodity Channel Index (CCI) signal              |
//+------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getCCISignal(const int cciHandle) {

   double cciValue[1];  // Array to store the current CCI value

   // CopyBuffer for CCI to get the current bar value
   if(CopyBuffer(cciHandle, 0, 1, 1, cciValue) > 0) {   // CCI buffer

      // Check for overbought/oversold conditions based on CCI value
      if(cciValue[0] < cciOversold) {
         //BUY;  // CCI is below -100, indicating a BUY signal (oversold)
         return BUY;
      } else if(cciValue[0] > cciOverbought) {
         //SELL; // CCI is above 100, indicating a SELL signal (overbought)
         return SELL;
      }
   } else {
      Print("Failed to retrieve CCI values.");
   }

   return NEUTRAL;  // If no signal or an error occurs, return NEUTRAL
}

//+------------------------------------------------------------------+
//| Function to get Relative Vigor Index (RVI) signal                 |
//+------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getRVISignal(const int rviHandle) {

   double rviLine[1], signalLine[1];  // Arrays to store current and previous RVI and Signal values

   // CopyBuffer for RVI line and Signal line to get the last 2 values (current and previous bars)
   if(CopyBuffer(rviHandle, 0, 1, 1, rviLine) > 0 &&   // RVI Line buffer
         CopyBuffer(rviHandle, 1, 1, 1, signalLine) > 0) { // Signal Line buffer

      // Check for RVI crossovers
      if(rviLine[0] < 0 && signalLine[0] < 0 && rviLine[0] > signalLine[0]) {
         //BUY;  // RVI line crosses above Signal line, indicating a BUY signal
         return BUY;
      } else if(rviLine[0] > 0 && signalLine[0] > 0 && rviLine[0] < signalLine[0]) {
         //SELL; // RVI line crosses below Signal line, indicating a SELL signal
         return SELL;
      }
   } else {
      Print("Failed to retrieve RVI values.");
   }

   return NEUTRAL;  // If no crossover or an error occurs, return NEUTRAL
}


//+------------------------------------------------------------------+
//| Função para abrir uma operação                                   |
//+------------------------------------------------------------------+
void OpenTrade(const ENUM_ORDER_TYPE orderType, const double tp, const double sl, const ENUM_TIMEFRAMES period) {

   MqlTradeRequest request;
   MqlTradeResult result;
   double price;

   // Defina preços com base no tipo de pedido
   if (orderType == ORDER_TYPE_BUY) {
      price = SymbolInfoDouble(symbolName, SYMBOL_ASK);
   } else if (orderType == ORDER_TYPE_SELL) {
      price = SymbolInfoDouble(symbolName, SYMBOL_BID);
   } else {
      Print("Invalido orderType");
      return;
   }

   // Preenche a estrutura do pedido de negociação
   ZeroMemory(request);
   request.action = TRADE_ACTION_DEAL;
   request.symbol = symbolName;
   request.volume = lotSize;
   request.type = orderType;
   request.price = price;
   request.sl = sl;
   request.tp = tp;
   request.comment = symbolName + "/" + EnumToString(period);

   // Envia o pedido de negociação
   if (!OrderSend(request, result)) {
      Print("Error opening order: ", result.retcode);
   }

}

//+------------------------------------------------------------------+
//| Função para obter o dia da semana                                |
//+------------------------------------------------------------------+
ENUM_DAY_OF_WEEK TimeDayOfWeek(const datetime time) {
   MqlDateTime mt;
   TimeToStruct(time, mt);
   return (ENUM_DAY_OF_WEEK) mt.day_of_week;
}

//+------------------------------------------------------------------+
//| Função para obter a hora do dia                                  |
//+------------------------------------------------------------------+
int TimeHour(const datetime time) {
   MqlDateTime mt;
   TimeToStruct(time, mt);
   return mt.hour;
}

//+------------------------------------------------------------------+
//| Função para verificar se o horário é permitido                   |
//+------------------------------------------------------------------+
bool IsTradingTimeAllowed(const datetime lastTime, const ENUM_DAY_OF_WEEK tradingDay, const ENUM_TIME_INTERVALS timeInterval) {
   ENUM_DAY_OF_WEEK currentWeek = TimeDayOfWeek(lastTime);
   if(currentWeek != tradingDay) {
      return false;
   } else {
      int currentHour = TimeHour(lastTime);
      switch (timeInterval) {
      case Interval_00_04:
         return (currentHour >= 0 && currentHour < 4);
      case Interval_04_08:
         return (currentHour >= 4 && currentHour < 8);
      case Interval_08_12:
         return (currentHour >= 8 && currentHour < 12);
      case Interval_12_16:
         return (currentHour >= 12 && currentHour < 16);
      case Interval_16_20:
         return (currentHour >= 16 && currentHour < 20);
      case Interval_20_24:
         return (currentHour >= 20 && currentHour < 24);
      default:
         return false;
      }
   }
}

//+------------------------------------------------------------------+
//| Função OnDeinit                                                  |
//+------------------------------------------------------------------+
void OnDeinit(const int reason) {
   // Release the ADX indicator HandleM15
   if(adxHandleM15 != INVALID_HANDLE) {
      IndicatorRelease(adxHandleM15);
   }

   // Release the RSI indicator HandleM15
   if(rsiHandleM15 != INVALID_HANDLE) {
      IndicatorRelease(rsiHandleM15);
   }

   // Release the AC HandleM15
   if(acHandleM15 != INVALID_HANDLE) {
      IndicatorRelease(acHandleM15);
   }

   // Release the MACD HandleM15
   if(macdHandleM15 != INVALID_HANDLE) {
      IndicatorRelease(macdHandleM15);
   }

   // Release the Stochastic HandleM15
   if(stochasticHandleM15 != INVALID_HANDLE) {
      IndicatorRelease(stochasticHandleM15);
   }

   // Release the %R HandleM15
   if(percentRangeHandleM15 != INVALID_HANDLE) {
      IndicatorRelease(percentRangeHandleM15);
   }

   // Release the CCI HandleM15
   if(cciHandleM15 != INVALID_HANDLE) {
      IndicatorRelease(cciHandleM15);
   }

   // Release the RVI HandleM15
   if(rviHandleM15 != INVALID_HANDLE) {
      IndicatorRelease(rviHandleM15);
   }

   // Release the ADX indicator HandleM30
   if(adxHandleM30 != INVALID_HANDLE) {
      IndicatorRelease(adxHandleM30);
   }

   // Release the RSI indicator HandleM30
   if(rsiHandleM30 != INVALID_HANDLE) {
      IndicatorRelease(rsiHandleM30);
   }

   // Release the AC HandleM30
   if(acHandleM30 != INVALID_HANDLE) {
      IndicatorRelease(acHandleM30);
   }

   // Release the MACD HandleM30
   if(macdHandleM30 != INVALID_HANDLE) {
      IndicatorRelease(macdHandleM30);
   }

   // Release the Stochastic HandleM30
   if(stochasticHandleM30 != INVALID_HANDLE) {
      IndicatorRelease(stochasticHandleM30);
   }

   // Release the %R HandleM30
   if(percentRangeHandleM30 != INVALID_HANDLE) {
      IndicatorRelease(percentRangeHandleM30);
   }

   // Release the CCI HandleM30
   if(cciHandleM30 != INVALID_HANDLE) {
      IndicatorRelease(cciHandleM30);
   }

   // Release the RVI HandleM30
   if(rviHandleM30 != INVALID_HANDLE) {
      IndicatorRelease(rviHandleM30);
   }

   // Release the ADX indicator HandleH1
   if(adxHandleH1 != INVALID_HANDLE) {
      IndicatorRelease(adxHandleH1);
   }

   // Release the RSI indicator HandleH1
   if(rsiHandleH1 != INVALID_HANDLE) {
      IndicatorRelease(rsiHandleH1);
   }

   // Release the AC HandleH1
   if(acHandleH1 != INVALID_HANDLE) {
      IndicatorRelease(acHandleH1);
   }

   // Release the MACD HandleH1
   if(macdHandleH1 != INVALID_HANDLE) {
      IndicatorRelease(macdHandleH1);
   }

   // Release the Stochastic HandleH1
   if(stochasticHandleH1 != INVALID_HANDLE) {
      IndicatorRelease(stochasticHandleH1);
   }

   // Release the %R HandleH1
   if(percentRangeHandleH1 != INVALID_HANDLE) {
      IndicatorRelease(percentRangeHandleH1);
   }

   // Release the CCI HandleH1
   if(cciHandleH1 != INVALID_HANDLE) {
      IndicatorRelease(cciHandleH1);
   }

   // Release the RVI HandleH1
   if(rviHandleH1 != INVALID_HANDLE) {
      IndicatorRelease(rviHandleH1);
   }
}

//+------------------------------------------------------------------+
//| Tester function                                                  |
//+------------------------------------------------------------------+
double OnTester() {
   //--- get trade results to the array
   double array[];
   double trades_volume;
   GetTradeResultsToArray(array, trades_volume);

   int trades = ArraySize(array);
   int profitable_trades = 0;

   //--- if there are no trades, return 0
   if (trades == 0)
      return 0;

   //--- count the number of profitable trades
   for (int i = 0; i < trades; i++) {
      if (array[i] > 0) // lucro positivo
         profitable_trades++;
   }

   //--- calculate the percentage of profitable trades (win rate)
   double win_rate = (double)profitable_trades / trades * 100.0;

   //--- display the message for the single-test mode
   if (MQLInfoInteger(MQL_TESTER) && !MQLInfoInteger(MQL_OPTIMIZATION))
      PrintFormat("%s: Trades=%d, Profitable trades=%d, Win rate=%.2f%%", __FUNCTION__, trades, profitable_trades, win_rate);

   //--- return the win rate as the custom optimization criterion
   return win_rate;
}

//+------------------------------------------------------------------+
//| Get the array of profits/losses from deals                       |
//+------------------------------------------------------------------+
bool GetTradeResultsToArray(double &pl_results[], double &volume) {
   //--- request the complete trading history
   if (!HistorySelect(0, TimeCurrent()))
      return (false);

   uint total_deals = HistoryDealsTotal();
   volume = 0;

   //--- set the initial size of the array with a margin - by the number of deals in history
   ArrayResize(pl_results, total_deals);

   //--- counter of deals that fix the trading result - profit or loss
   int counter = 0;
   ulong ticket_history_deal = 0;

   //--- go through all deals
   for (uint i = 0; i < total_deals; i++) {
      //--- select a deal
      if ((ticket_history_deal = HistoryDealGetTicket(i)) > 0) {
         ENUM_DEAL_ENTRY deal_entry  = (ENUM_DEAL_ENTRY)HistoryDealGetInteger(ticket_history_deal, DEAL_ENTRY);
         long            deal_type   = HistoryDealGetInteger(ticket_history_deal, DEAL_TYPE);
         double          deal_profit = HistoryDealGetDouble(ticket_history_deal, DEAL_PROFIT);
         double          deal_volume = HistoryDealGetDouble(ticket_history_deal, DEAL_VOLUME);

         //--- we are only interested in trading operations
         if ((deal_type != DEAL_TYPE_BUY) && (deal_type != DEAL_TYPE_SELL))
            continue;

         //--- only deals that fix profits/losses
         if (deal_entry != DEAL_ENTRY_IN) {
            //--- write the trading result to the array and increase the counter of deals
            pl_results[counter] = deal_profit;
            volume += deal_volume;
            counter++;
         }
      }
   }

   //--- set the final size of the array
   ArrayResize(pl_results, counter);
   return (true);
}
//+------------------------------------------------------------------+
