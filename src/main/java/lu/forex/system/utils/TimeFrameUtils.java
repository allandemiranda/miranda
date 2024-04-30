package lu.forex.system.utils;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lu.forex.system.enums.TimeFrame;

public class TimeFrameUtils {

  private TimeFrameUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static @NotNull LocalDateTime getCandlestickDateTime(final @NotNull LocalDateTime timestamp, final @NotNull TimeFrame timeFrame) {
    return switch (timeFrame.getFrame()) {
      case MINUTE -> getMinuteTime(timestamp, timeFrame);
      case HOUR -> getHourTime(timestamp);
      case DAY -> getDayTime(timestamp);
    };
  }

  private static @NotNull LocalDateTime getDayTime(final @NotNull LocalDateTime timestamp) {
    return LocalDateTime.of(timestamp.toLocalDate(), LocalTime.of(0, 0, 0));
  }

  private static @NotNull LocalDateTime getHourTime(final @NotNull LocalDateTime timestamp) {
    return LocalDateTime.of(timestamp.toLocalDate(), LocalTime.of(timestamp.getHour(), 0, 0));
  }

  private static @NotNull LocalDateTime getMinuteTime(final @NotNull LocalDateTime timestamp, final @NotNull TimeFrame timeFrame) {
    final int div = timestamp.getMinute() / timeFrame.getTimeValue();
    final int newMinute = div * timeFrame.getTimeValue();
    final LocalTime candlestickTime = LocalTime.of(timestamp.getHour(), newMinute, 0);
    return LocalDateTime.of(timestamp.toLocalDate(), candlestickTime);
  }
}
