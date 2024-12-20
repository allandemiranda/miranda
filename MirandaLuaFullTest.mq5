//+------------------------------------------------------------------+
//|                                                   MirandaLua.mq5 |
//|                                                 Allan de Miranda |
//|                               https://github.com/allandemiranda/ |
//+------------------------------------------------------------------+
#property copyright "Allan de Miranda"
#property link      "https://github.com/allandemiranda/"
#property version   "1.20"

#include <Trade\Trade.mqh>
#include <Trade\PositionInfo.mqh>
#include <WinINet.mqh>

// Tamanho do lote
input double tradeLotSize = 0.01;

// OpenAI
input string openAiKey = "API_KEY";
input string openAiModel = "gpt-4o";
input int openAiCandlesAnalisys = 100;

// Parâmetros do ADX
input int adxPeriod = 14;
input double adxLevel = 25;

// Parâmetros do Stochastic
input int stochasticKperiod = 5;
input int stochasticDperiod = 3;
input int stochasticSlowing = 3;
input ENUM_MA_METHOD stochasticMaMethod = MODE_SMA;
input ENUM_STO_PRICE stochasticPriceField = STO_LOWHIGH;
input double stochasticOverbought = 80;
input double stochasticOversold = 20;

// Sinal do indicador
enum ENUM_SIGNAL_INDICATOR {
   BUY,
   SELL,
   NEUTRAL
};

// Trade
CTrade trade;
CPositionInfo position;

// AI Signal
struct SignalAI {
   ENUM_SIGNAL_INDICATOR signal;
   double tp;
   double sl;
};

// Variável para armazenar o manipulador dos indicatores
int adxHandle, stochasticHandle;

// Variável para armazenar o timestamp do último candlestick criado
datetime cacheLastCandleTime = 0;

// Função OnInit
int OnInit(void) {

   // Iniciando indicador ADX
   adxHandle = iADX(Symbol(), ChartPeriod(), adxPeriod);
   if(adxHandle == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iADX, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   // Iniciando indicador Stochastic
   stochasticHandle = iStochastic(Symbol(), ChartPeriod(), stochasticKperiod, stochasticDperiod, stochasticSlowing, stochasticMaMethod, stochasticPriceField);
   if(stochasticHandle == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iStochastic, código de erro %d", GetLastError());
      return(INIT_FAILED);
   }

   return(INIT_SUCCEEDED);
}

// Função para verificar se existe um novo candle
bool HaveNewCandle(const string symbolName, const ENUM_TIMEFRAMES timeframe) {
   const datetime lastCandleTime = iTime(symbolName, timeframe, 1);
   if(cacheLastCandleTime != lastCandleTime) {
      cacheLastCandleTime = lastCandleTime;
      return true;
   } else {
      return false;
   }
}

// Função para obter o sinal do indicador ADX
ENUM_SIGNAL_INDICATOR GetAdxSignal(const int handle, const double level) {
   double adxValue[1], plusDIValue[1], minusDIValue[1];
   if(CopyBuffer(handle, 0, 1, 1, adxValue) > 0 && CopyBuffer(handle, 1, 1, 1, plusDIValue) > 0 && CopyBuffer(handle, 2, 1, 1, minusDIValue) > 0) {
      if(adxValue[0] >= level) {
         if(plusDIValue[0] > minusDIValue[0]) {
            return BUY;
         } else if(plusDIValue[0] < minusDIValue[0]) {
            return SELL;
         }
      }
   } else {
      Print("Falha ao recuperar valores no indicador ADX.");
   }
   return NEUTRAL;
}

// Função para obter o sinal do indicador Stochastic
ENUM_SIGNAL_INDICATOR GetStochasticSignal(const int handle, const double oversold, const double overbought) {
   double kLine[1], dLine[1];
   if(CopyBuffer(handle, 0, 1, 1, kLine) > 0 && CopyBuffer(handle, 1, 1, 1, dLine) > 0) {
      if(kLine[0] < oversold && dLine[0] < oversold) {
         return BUY;
      } else if(kLine[0] > overbought && dLine[0] > overbought) {
         return SELL;
      }
   } else {
      Print("Falha ao recuperar valores no indicador Stochastic.");
   }
   return NEUTRAL;
}

// Função para obter o texto do enum do timeframe
string GetTimeframeName(const ENUM_TIMEFRAMES timeframe) {
   string parts[];
   StringSplit(EnumToString(timeframe), '_', parts);
   return parts[1];
}

// Função para coletar dados dos últimos candles
string GetCandleData(const ENUM_TIMEFRAMES timeframe, const int candlesNumForAnalysis) {
   string candleData = "";
   for (int i = 1; i <= candlesNumForAnalysis; i++) {
      const double open = iOpen(Symbol(), timeframe, i);
      const double close = iClose(Symbol(), timeframe, i);
      const double high = iHigh(Symbol(), timeframe, i);
      const double low = iLow(Symbol(), timeframe, i);
      const long volume = iVolume(Symbol(), timeframe, i);
      candleData += StringFormat("Candle %d: Open=%.5f, Close=%.5f, High=%.5f, Low=%.5f, Volume=%d\\n", i, open, close, high, low, volume);
   }
   return candleData;
}

// Função para obter toda a mensagem de resposta da OpenAI
string GetMessageAIContent(const string jsonResponse) {
   // Define as strings de início e fim para localizar o conteúdo desejado
   const string startPattern = "\"content\": \"";
   const string endPattern = "\"";

   // Localiza a posição inicial do conteúdo de "content"
   int startPos = StringFind(jsonResponse, startPattern);
   if (startPos == -1) {
      Print("Erro: 'content' não encontrado no JSON.");
      return "";
   }

   // Ajusta a posição para depois do startPattern
   startPos += StringLen(startPattern);

   // Localiza a posição de fechamento do conteúdo de "content"
   int endPos = StringFind(jsonResponse, endPattern, startPos);
   if (endPos == -1) {
      Print("Erro: Fim de 'content' não encontrado no JSON.");
      return "";
   }

   // Extrai o conteúdo de "content"
   return StringSubstr(jsonResponse, startPos, endPos - startPos);
}

// Função para processar a resposta da OpenAI
SignalAI ParseOpenAIResponse(const string jsonResponse, const string symbolName, const ENUM_TIMEFRAMES timeframe) {
   SignalAI aiSignal;
   aiSignal.signal = NEUTRAL;
   aiSignal.tp = 0.0;
   aiSignal.sl = 0.0;

   const string aiMsgResponse = GetMessageAIContent(jsonResponse);
   if(aiMsgResponse == "") {
      Print("OpenAI JSON: ", jsonResponse);
   } else {
      Print("OpenAI response: ", aiMsgResponse);
   }

   if (StringFind(aiMsgResponse, "buy") >= 0) {
      aiSignal.signal = BUY;
   } else if (StringFind(aiMsgResponse, "sell") >= 0) {
      aiSignal.signal = SELL;
   } else {
      Print("OpenAI decidiu não abrir uma operação ", symbolName, "/", EnumToString(timeframe));
      return aiSignal;
   }

   int tpStart = StringFind(aiMsgResponse, "TP:") + 3;
   if (tpStart > 2) {
      int tpEnd = StringFind(aiMsgResponse, ",", tpStart);
      if (tpEnd > tpStart) {
         aiSignal.tp = StringToDouble(StringSubstr(aiMsgResponse, tpStart, tpEnd - tpStart));
      }
   }

   int slStart = StringFind(aiMsgResponse, "SL:") + 3;
   if (slStart > 2) {
      aiSignal.sl = StringToDouble(StringSubstr(aiMsgResponse, slStart));
   }

   if(aiSignal.tp > 0.0 && aiSignal.sl > 0.0) {
      return aiSignal;
   } else {
      Print("OpenAI retornou mal TP ou SL. TP=", aiSignal.tp, ", SL=", aiSignal.sl, ", msg=", aiMsgResponse);
      aiSignal.signal = NEUTRAL;
      return aiSignal;
   }
}

// Função para obter sinal da OpenAI com dados de candles
SignalAI GetOpenAISignal(const ENUM_TIMEFRAMES timeframe, const string symbolName, const string modelAI, const int candlesNumForAnalysis, const string keyAI) {
   const string timeFrameName = GetTimeframeName(timeframe);

   const int maxTokens = 20;
   const double temperature = 0.2;
   const int topP = 1;
   const int frequencyPenalty = 0;
   const int presencePenalty = 0;

   const string systemMsg = "{\"role\":\"system\",\"content\":\"You are providing a Forex market analysis for a " + symbolName + " currency pair and timeframe " + timeFrameName +". You will receive the latest " + IntegerToString(candlesNumForAnalysis) + " candles, including Open price, Close price, High price, Low price, and Volume. Based on this data and in your data forex past information, combine Fibonacci Retracement with Support/Resistance, Candlestick patterns, Trading Trend Patterns, trend indicators, oscillators indicators, and Bill Williams indicators. Your response should recommend whether to open a buy or a sell position, including suggested Take Profit (TP) and Stop Loss (SL) levels, or to refrain from opening a position if conditions are not good, after your best data analysis. Provide only the decision and values, without additional explanation.\"}";
   const string contentUser = "Pair: " + symbolName + "\\nTimeframe: " + timeFrameName + "\\nList of candles from newest to oldest:\\n" + GetCandleData(timeframe, candlesNumForAnalysis) + "Based on the provided data, determine if I should open a buy or sell position, including TP and SL, or if no action should be taken. Respond with: 'Order: [buy/sell/not], TP: [price], SL: [price]'";
   const string userMsg = "{\"role\":\"user\", \"content\": \"" + contentUser + "\"}";
   const string postData = "{\"model\":\"" + modelAI + "\",\"max_tokens\":" + IntegerToString(maxTokens) + ",\"temperature\":" + DoubleToString(temperature) + ",\"top_p\":" + IntegerToString(topP) + ",\"frequency_penalty\":" + IntegerToString(frequencyPenalty) + ",\"presence_penalty\":" + IntegerToString(presencePenalty) + ",\"messages\":[" + systemMsg + "," + userMsg + "]}";

   char postDataArray[];
   StringToCharArray(postData, postDataArray);

   char result[];
   string resultHeaders;

   const string method = "POST";
   const string host = "api.openai.com";
   const int port = 443;
   const string path = "/v1/chat/completions";
   const string headers = "Content-Type: application/json\r\nAuthorization: Bearer " + keyAI;

   const int code = WebReq(method, host, path, port, true, headers, postDataArray, result, resultHeaders);

   if (code != 200) {
      Print("Erro na requisição para OpenAI: codeError=", GetLastError(), " HTTP=", code);
      SignalAI aiSignal;
      aiSignal.signal = NEUTRAL;
      aiSignal.tp = 0.0;
      aiSignal.sl = 0.0;
      return aiSignal;
   } else {
      const string jsonResponse = CharArrayToString(result);
      return ParseOpenAIResponse(jsonResponse, symbolName, timeframe);
   }
}

// Função para abrir uma operação
void OpenTrade(const ENUM_ORDER_TYPE orderType, const double takeProfit, const double stopLoss, const double lotSize, const string symbolName, const ENUM_TIMEFRAMES timeframe) {

   MqlTradeRequest request;
   MqlTradeResult result;
   double price;

   if (orderType == ORDER_TYPE_BUY) {
      price = SymbolInfoDouble(symbolName, SYMBOL_ASK);
   } else if (orderType == ORDER_TYPE_SELL) {
      price = SymbolInfoDouble(symbolName, SYMBOL_BID);
   } else {
      Print("Order Type inválido, operação não aberta.");
      return;
   }

   ZeroMemory(request);
   request.action = TRADE_ACTION_DEAL;
   request.symbol = symbolName;
   request.volume = lotSize;
   request.type = orderType;
   request.price = price;
   request.sl = stopLoss;
   request.tp = takeProfit;
   request.comment = symbolName + "/" + EnumToString(timeframe);

   // Envia o pedido de negociação
   if (!OrderSend(request, result)) {
      Print("Erro ao abrir orden ", request.comment, ": ", result.retcode);
   } else {
      Print("Ordem aberta: ", request.comment);
   }
}

// Função OnTick
void OnTick(void) {
   const ENUM_TIMEFRAMES timeframe = ChartPeriod();
   const string symbolName = Symbol();

   if(HaveNewCandle(symbolName, timeframe)) {
      const ENUM_SIGNAL_INDICATOR adxSignal = GetAdxSignal(adxHandle, adxLevel);
      const ENUM_SIGNAL_INDICATOR stochasticSignal = GetStochasticSignal(stochasticHandle, stochasticOversold, stochasticOverbought);
      if(adxSignal != NEUTRAL && stochasticSignal != NEUTRAL) {
         const SignalAI aiSignal = GetOpenAISignal(timeframe, symbolName, openAiModel, openAiCandlesAnalisys, openAiKey);
         if(aiSignal.signal == BUY) {
            OpenTrade(ORDER_TYPE_BUY, aiSignal.tp, aiSignal.sl, tradeLotSize, symbolName, timeframe);
         } else if(aiSignal.signal == SELL) {
            OpenTrade(ORDER_TYPE_SELL, aiSignal.tp, aiSignal.sl, tradeLotSize, symbolName, timeframe);
         }
      }
   }
}


// Função OnDeinit
void OnDeinit(const int reason) {
   // Release the ADX indicator Handle
   if(adxHandle != INVALID_HANDLE) {
      IndicatorRelease(adxHandle);
   }

   // Release the Stochastic Handle
   if(stochasticHandle != INVALID_HANDLE) {
      IndicatorRelease(stochasticHandle);
   }
}

//+------------------------------------------------------------------+
