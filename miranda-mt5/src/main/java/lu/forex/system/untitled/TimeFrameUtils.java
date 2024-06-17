package lu.forex.system.untitled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeFrameUtils {

  public static LocalDateTime getCandlestickTimestamp(final LocalDateTime timestamp, final TimeFrame timeFrame) {
    return switch (timeFrame.getFrame()) {
      case MINUTE -> getMinuteTime(timestamp, timeFrame);
      case HOUR -> getHourTime(timestamp, timeFrame);
      case DAY -> getDayTime(timestamp, timeFrame);
    };
  }

  private static LocalDateTime getDayTime(final LocalDateTime timestamp, final TimeFrame timeFrame) {
    final LocalTime localTime = LocalTime.of(0, 0, 0);
    if (timestamp.getDayOfMonth() % timeFrame.getTimeValue() == 0) {
      return LocalDateTime.of(LocalDate.of(timestamp.getYear(), timestamp.getMonth(), timestamp.getDayOfMonth() - (timeFrame.getTimeValue() - 1)),
          localTime);
    } else {
      final int div = timestamp.getDayOfMonth() / timeFrame.getTimeValue();
      final int newDay = div * timeFrame.getTimeValue();
      return LocalDateTime.of(LocalDate.of(timestamp.getYear(), timestamp.getMonth(), newDay + 1), localTime);
    }
  }

  private static LocalDateTime getHourTime(final LocalDateTime timestamp, final TimeFrame timeFrame) {
    final int div = timestamp.getHour() / timeFrame.getTimeValue();
    final int newHour = div * timeFrame.getTimeValue();
    final LocalTime candlestickTime = LocalTime.of(newHour, 0, 0);
    return LocalDateTime.of(timestamp.toLocalDate(), candlestickTime);
  }

  private static LocalDateTime getMinuteTime(final LocalDateTime timestamp, final TimeFrame timeFrame) {
    final int div = timestamp.getMinute() / timeFrame.getTimeValue();
    final int newMinute = div * timeFrame.getTimeValue();
    final LocalTime candlestickTime = LocalTime.of(timestamp.getHour(), newMinute, 0);
    return LocalDateTime.of(timestamp.toLocalDate(), candlestickTime);
  }
}
