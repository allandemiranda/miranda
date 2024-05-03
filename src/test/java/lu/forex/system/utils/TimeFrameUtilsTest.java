package lu.forex.system.utils;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lu.forex.system.enums.TimeFrame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class TimeFrameUtilsTest {

  private static int getRandomInt(final int min, final int max) {
    final Random random = new Random(LocalDateTime.now().getNano());
    return random.nextInt(max - min + 1) + min;
  }

  private static @NotNull Stream<LocalDateTime> getRandomDateTimes() {
    final ArrayList<Integer> yearsList = new ArrayList<>(getIntegers(10, 2000, 2020));
    final ArrayList<Integer> monthsList = new ArrayList<>(getIntegers(10, 1, 12));
    final ArrayList<Integer> daysList = new ArrayList<>(getIntegers(10, 1, 27));
    final ArrayList<Integer> hoursList = new ArrayList<>(getIntegers(10, 1, 23));
    final ArrayList<Integer> minutesAndSecondsList = new ArrayList<>(getIntegers(20, 0, 59));
    return IntStream.range(0, 10).mapToObj(i -> {
      final int randomIndexYear = getRandomInt(0, yearsList.size() - 1);
      final int year = yearsList.get(randomIndexYear);
      yearsList.remove(randomIndexYear);
      final int randomIndexMonth = getRandomInt(0, monthsList.size() - 1);
      final int month = monthsList.get(randomIndexMonth);
      monthsList.remove(randomIndexMonth);
      final int randomIndexDay = getRandomInt(0, daysList.size() - 1);
      final int day = daysList.get(randomIndexDay);
      daysList.remove(randomIndexDay);
      final int randomIndexHours = getRandomInt(0, hoursList.size() - 1);
      final int hour = hoursList.get(randomIndexHours);
      hoursList.remove(randomIndexHours);
      final int randomIndexMinute = getRandomInt(0, minutesAndSecondsList.size() - 1);
      final int minute = minutesAndSecondsList.get(randomIndexMinute);
      minutesAndSecondsList.remove(randomIndexMinute);
      final int randomIndexSecond = getRandomInt(0, minutesAndSecondsList.size() - 1);
      final int second = minutesAndSecondsList.get(randomIndexSecond);
      minutesAndSecondsList.remove(randomIndexSecond);
      return LocalDateTime.of(year, month, day, hour, minute, second);
    });
  }

  private static @NotNull HashSet<Integer> getIntegers(final int size, final int min, final int max) {
    HashSet<Integer> hashSet = new HashSet<>();
    while (hashSet.size() < size) {
      hashSet.add(getRandomInt(min, max));
    }
    return hashSet;
  }

  @ParameterizedTest
  @MethodSource("getRandomDateTimes")
  void getCandlestickDateTime15MinuteTimeFrame(LocalDateTime timestamp) {
    //given
    final TimeFrame timeFrame = TimeFrame.M15;
    System.out.println(timestamp);

    //when
    final LocalDateTime result = TimeFrameUtils.getCandlestickDateTime(timestamp, timeFrame);
    final int resultMinute = result.toLocalTime().getMinute();

    //then
    Assertions.assertEquals(timestamp.toLocalDate(), result.toLocalDate());
    Assertions.assertEquals(timestamp.toLocalTime().getHour(), result.toLocalTime().getHour());
    Assertions.assertTrue(resultMinute == 0 || resultMinute == 15 || resultMinute == 30 || resultMinute == 45);
    Assertions.assertEquals(0, result.toLocalTime().getSecond());
  }

  @ParameterizedTest
  @MethodSource("getRandomDateTimes")
  void getCandlestickDateTime30MinuteTimeFrame(LocalDateTime timestamp) {
    //given
    final TimeFrame timeFrame = TimeFrame.M30;
    System.out.println(timestamp);

    //when
    final LocalDateTime result = TimeFrameUtils.getCandlestickDateTime(timestamp, timeFrame);
    final int resultMinute = result.toLocalTime().getMinute();

    //then
    Assertions.assertEquals(timestamp.toLocalDate(), result.toLocalDate());
    Assertions.assertEquals(timestamp.toLocalTime().getHour(), result.toLocalTime().getHour());
    Assertions.assertTrue(resultMinute == 0 || resultMinute == 30);
    Assertions.assertEquals(0, result.toLocalTime().getSecond());
  }

  @ParameterizedTest
  @MethodSource("getRandomDateTimes")
  void getCandlestickDateTime1HourTimeFrame(LocalDateTime timestamp) {
    //given
    final TimeFrame timeFrame = TimeFrame.H1;
    System.out.println(timestamp);

    //when
    final LocalDateTime result = TimeFrameUtils.getCandlestickDateTime(timestamp, timeFrame);

    //then
    Assertions.assertEquals(timestamp.toLocalDate(), result.toLocalDate());
    Assertions.assertEquals(timestamp.toLocalTime().getHour(), result.toLocalTime().getHour());
    Assertions.assertEquals(0, result.toLocalTime().getMinute());
    Assertions.assertEquals(0, result.toLocalTime().getSecond());
  }

  @ParameterizedTest
  @MethodSource("getRandomDateTimes")
  void getCandlestickDateTime4HourTimeFrame(LocalDateTime timestamp) {
    //given
    final TimeFrame timeFrame = TimeFrame.H4;
    System.out.println(timestamp);

    //when
    final LocalDateTime result = TimeFrameUtils.getCandlestickDateTime(timestamp, timeFrame);
    final int resultHour = result.toLocalTime().getHour();

    //then
    Assertions.assertEquals(timestamp.toLocalDate(), result.toLocalDate());
    Assertions.assertTrue(resultHour == 0 || resultHour == 4 || resultHour == 8 || resultHour == 12 || resultHour == 16 || resultHour == 20);
    Assertions.assertEquals(0, result.toLocalTime().getMinute());
    Assertions.assertEquals(0, result.toLocalTime().getSecond());
  }

  @ParameterizedTest
  @MethodSource("getRandomDateTimes")
  void getCandlestickDateTime1DayTimeFrame(LocalDateTime timestamp) {
    //given
    final TimeFrame timeFrame = TimeFrame.D1;
    System.out.println(timestamp);

    //when
    final LocalDateTime result = TimeFrameUtils.getCandlestickDateTime(timestamp, timeFrame);

    //then
    Assertions.assertEquals(timestamp.toLocalDate(), result.toLocalDate());
    Assertions.assertEquals(0, result.toLocalTime().getHour());
    Assertions.assertEquals(0, result.toLocalTime().getMinute());
    Assertions.assertEquals(0, result.toLocalTime().getSecond());
  }
}