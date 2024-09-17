//+------------------------------------------------------------------+
//|                                                   MirandaLua.mq5 |
//|                                                 Allan de Miranda |
//|                               https://github.com/allandemiranda/ |
//+------------------------------------------------------------------+
#property copyright "Allan de Miranda"
#property link      "https://github.com/allandemiranda/"
#property version   "1.05"

//+------------------------------------------------------------------+
//| Expert initialization function                                   |
//+------------------------------------------------------------------+
input string symbol = "EURUSD";    // Símbolo
input ENUM_TIMEFRAMES timeframe = PERIOD_H1; // Timeframe
input double lotSize = 0.01; // Tamanho do lote

// Definição do dia da semana e intervalo de horas permitidos
input ENUM_DAY_OF_WEEK tradingDay = MONDAY;   // Dia da semana para negociação
input int StartHour = 4;                      // Hora de início para o dia configurado
input int EndHour = 8;                        // Hora de término para o dia configurado

// Parâmetros do ADX
input int adxPeriod = 14;
input double adxLevel = 40;

// Parâmetros do RSI
input int rsiPeriod = 14;
input double rsiOverbought = 70;
input double rsiOversold = 30;

// Take Profit e Stop Loss em pontos
input int takeProfit = 100; // TP em pontos
input int stopLoss = 70;    // SL em pontos

// Variável global para armazenar o número da última barra verificada
int lastBar = 0;

//+------------------------------------------------------------------+
//| Expert tick function                                             |
//+------------------------------------------------------------------+
void OnTick()
{
    static int currentBar = 0;  // Local static variable for bar tracking

    // Only update if there's a new bar
    currentBar = iBars(symbol, timeframe);
    if (currentBar <= lastBar)
        return;  // No new candle, skip further execution

    lastBar = currentBar;  // Update last checked bar

    // Check if within allowed trading time, only at new bar
    if (!IsTradingTimeAllowed())
        return;

    // Evaluate the ADX and RSI indicators at the close of the last bar
    double adxValue = iADX(symbol, timeframe, adxPeriod);
    double rsiValue = iRSI(symbol, timeframe, rsiPeriod, PRICE_CLOSE);

    // Skip further logic if ADX does not indicate strong trend
    if (adxValue < adxLevel)
        return;

    // Check for Buy/Sell signals based on RSI
    bool buySignal = (rsiValue < rsiOversold);
    bool sellSignal = (rsiValue > rsiOverbought);

    // Only open a trade if no active orders exist
    if (OrdersTotal() == 0)
    {
        if (buySignal)
        {
            OpenTrade(ORDER_TYPE_BUY);
        }
        else if (sellSignal)
        {
            OpenTrade(ORDER_TYPE_SELL);
        }
    }
}
//+------------------------------------------------------------------+
//| Função para obter o dia da semana                                |
//+------------------------------------------------------------------+
int TimeDayOfWeek(datetime time) {
   MqlDateTime mt;
   bool turn=TimeToStruct(time,mt);
   return(mt.day_of_week);
}
//+------------------------------------------------------------------+
//| Função para obter a hora do dia                                  |
//+------------------------------------------------------------------+
int TimeHour(datetime time) {
   MqlDateTime mt;
   bool turn=TimeToStruct(time,mt);
   return(mt.hour);
}
//+------------------------------------------------------------------+
//| Função para verificar se o horário é permitido                   |
//+------------------------------------------------------------------+
bool IsTradingTimeAllowed()
{
    datetime currentTime = TimeCurrent();
    int currentDay = TimeDayOfWeek(currentTime);
    int currentHour = TimeHour(currentTime);

    // Verificar se o dia e o horário atual correspondem ao intervalo permitido
    if (currentDay == tradingDay)
        return (currentHour >= StartHour && currentHour < EndHour);

    return false; // Não permitido em outros dias ou horários
}
//+------------------------------------------------------------------+
//| Função para abrir uma operação                                   |
//+------------------------------------------------------------------+
void OpenTrade(int orderType)
{
    MqlTradeRequest request;
    MqlTradeResult result;

    double price, sl, tp;

    // Set prices based on order type
    if (orderType == ORDER_TYPE_BUY)
    {
        price = SymbolInfoDouble(symbol, SYMBOL_ASK);
        sl = price - stopLoss * _Point;
        tp = price + takeProfit * _Point;
    }
    else if (orderType == ORDER_TYPE_SELL)
    {
        price = SymbolInfoDouble(symbol, SYMBOL_BID);
        sl = price + stopLoss * _Point;
        tp = price - takeProfit * _Point;
    }

    // Fill the trade request structure
    ZeroMemory(request);
    request.action = TRADE_ACTION_DEAL;
    request.symbol = symbol;
    request.volume = lotSize;
    request.type = orderType;
    request.price = price;
    request.sl = sl;
    request.tp = tp;
    request.deviation = 2;
    request.comment = (orderType == ORDER_TYPE_BUY) ? "Buy Order" : "Sell Order";

    // Send the trade request
    if (!OrderSend(request, result))
    {
        Print("Error opening order: ", result.retcode);
    }
    else
    {
        // Determinar o dia e intervalo para o print
        string day = EnumToString(tradingDay);
        string timeRange = IntegerToString(StartHour) + "h-" + IntegerToString(EndHour) + "h";

        Print("Order opened successfully. Ticket: ", result.order, ", Symbol: ", symbol, ", TimeFrame: ", EnumToString(timeframe), ", Day: ", day, ", Time Range: ", timeRange);
    }
}
