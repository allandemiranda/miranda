//+------------------------------------------------------------------+
//|                                                   MirandaLua.mq5 |
//|                                                 Allan de Miranda |
//|                               https://github.com/allandemiranda/ |
//+------------------------------------------------------------------+
#property copyright "Allan de Miranda"
#property link      "https://github.com/allandemiranda/"
#property version   "1.09"

//+------------------------------------------------------------------+
//| Definição de ENUMs para Timeframes e Intervalos de Hora          |
//+------------------------------------------------------------------+
enum ENUM_ALLOWED_TIMEFRAMES
{
   M15 = PERIOD_M15,
   M30 = PERIOD_M30,
   H1 = PERIOD_H1
};

enum ENUM_TIME_INTERVALS
{
   Interval_00_04 = 0,
   Interval_04_08,
   Interval_08_12,
   Interval_12_16,
   Interval_16_20,
   Interval_20_24
};

//+------------------------------------------------------------------+
//| Inputs do robô                                                   |
//+------------------------------------------------------------------+
input string symbol = "EURUSD";    // Símbolo

// Lista de seleção dos timeframes permitidos
input ENUM_ALLOWED_TIMEFRAMES allowedTimeframe = H1; // Seleção de timeframes permitidos

// Definição do dia da semana e intervalo de horas permitidos
input ENUM_DAY_OF_WEEK tradingDay = MONDAY;   // Dia da semana para negociação (valores em maiúsculo)

// Lista de intervalos de horas permitidos
input ENUM_TIME_INTERVALS timeInterval = Interval_04_08; // Intervalo de horas permitidos

// Tamanho do lote
input double lotSize = 0.01; // Tamanho do lote para negociação

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
//| Função para converter ENUM_ALLOWED_TIMEFRAMES para ENUM_TIMEFRAMES|
//+------------------------------------------------------------------+
ENUM_TIMEFRAMES ConvertAllowedTimeframe(ENUM_ALLOWED_TIMEFRAMES inputTimeframe)
{
   switch(inputTimeframe)
   {
      case M15: return PERIOD_M15;
      case M30: return PERIOD_M30;
      case H1: return PERIOD_H1;
      default: return PERIOD_H1; // Valor padrão caso algo dê errado
   }
}

//+------------------------------------------------------------------+
//| Função OnTick                                                    |
//+------------------------------------------------------------------+
void OnTick()
{
    static int currentBar = 0;  // Local static variable for bar tracking
    ENUM_TIMEFRAMES timeframe = ConvertAllowedTimeframe(allowedTimeframe); // Converte o timeframe permitido para ENUM_TIMEFRAMES

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
            OpenTrade(ORDER_TYPE_BUY, timeframe);
        }
        else if (sellSignal)
        {
            OpenTrade(ORDER_TYPE_SELL, timeframe);
        }
    }
}

//+------------------------------------------------------------------+
//| Função para obter o dia da semana                                |
//+------------------------------------------------------------------+
int TimeDayOfWeek(datetime time) {
   MqlDateTime mt;
   TimeToStruct(time,mt);
   return(mt.day_of_week);
}

//+------------------------------------------------------------------+
//| Função para obter a hora do dia                                  |
//+------------------------------------------------------------------+
int TimeHour(datetime time) {
   MqlDateTime mt;
   TimeToStruct(time,mt);
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

    // Verifica se o dia é permitido
    if (currentDay != tradingDay)
        return false;

    // Verifica o intervalo de horas permitido com base no input `timeInterval`
    switch (timeInterval)
    {
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

//+------------------------------------------------------------------+
//| Função para abrir uma operação                                   |
//+------------------------------------------------------------------+
void OpenTrade(int orderType, ENUM_TIMEFRAMES timeframe)
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

    // Preenche a estrutura do pedido de negociação
    ZeroMemory(request);
    request.action = TRADE_ACTION_DEAL;
    request.symbol = symbol;
    request.volume = lotSize; // Usa o tamanho do lote configurado no input
    request.type = orderType;
    request.price = price;
    request.sl = sl;
    request.tp = tp;
    request.deviation = 2;
    request.comment = (orderType == ORDER_TYPE_BUY) ? "Buy Order" : "Sell Order";

    // Envia o pedido de negociação
    if (!OrderSend(request, result))
    {
        Print("Error opening order: ", result.retcode);
    }
    else
    {
        // Converte o intervalo de tempo para uma string legível
        string timeRange = "";
        switch (timeInterval)
        {
            case Interval_00_04:
                timeRange = "00:00h-03:59h";
                break;
            case Interval_04_08:
                timeRange = "04:00h-07:59h";
                break;
            case Interval_08_12:
                timeRange = "08:00h-11:59h";
                break;
            case Interval_12_16:
                timeRange = "12:00h-15:59h";
                break;
            case Interval_16_20:
                timeRange = "16:00h-19:59h";
                break;
            case Interval_20_24:
                timeRange = "20:00h-23:59h";
                break;
        }

        // Mensagem de sucesso ao abrir a ordem
        Print("Order opened successfully. Ticket: ", result.order, ", Symbol: ", symbol, ", LotSize: ", DoubleToString(lotSize, 2), ", TimeFrame: ", EnumToString(timeframe), ", Day: ", EnumToString(tradingDay), ", Time Range: ", timeRange);
    }
}
