package lu.forex.system.utils;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lu.forex.system.enums.Frame;
import lu.forex.system.enums.TimeFrame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TimeFrameUtilsTest {

  private static int getRandomInt(final int min, final int max) {
    final Random random = new Random(LocalDateTime.now().getNano());
    return random.nextInt(max - min + 1) + min;
  }

  private static @NotNull Stream<Arguments> getRandomDateTimes() {
    final ArrayList<Integer> yearsList = new ArrayList<>(getIntegers(10, 2000, 2020));
    final ArrayList<Integer> monthsList = new ArrayList<>(getIntegers(10, 1, 12));
    final ArrayList<Integer> daysList = new ArrayList<>(getIntegers(10, 1, 27));
    final ArrayList<Integer> hoursList = new ArrayList<>(getIntegers(10, 1, 23));
    final ArrayList<Integer> minutesAndSecondsList = new ArrayList<>(getIntegers(20, 0, 59));
    final Collection<LocalDateTime> localDateTimeStream = IntStream.range(0, 10).mapToObj(i -> {
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
    }).collect(Collectors.toCollection(ArrayList::new));
    return Stream.of(1, 2, 3, 4, 5, 6, 7, 10, 15, 30)
        .flatMap(timeValue -> localDateTimeStream.stream().map(localDateTime -> Arguments.arguments(localDateTime, timeValue)));
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
  void getCandlestickDateTimeMinuteTimeFrame(LocalDateTime timestamp, int timeValue) {
    //given
    final TimeFrame timeFrame = Mockito.mock(TimeFrame.class);

    //when
    Mockito.when(timeFrame.getTimeValue()).thenReturn(timeValue);
    Mockito.when(timeFrame.getFrame()).thenReturn(Frame.MINUTE);
    final LocalDateTime result = TimeFrameUtils.getCandlestickDateTime(timestamp, timeFrame);
    final int resultMinute = result.toLocalTime().getMinute();

    //then
    Assertions.assertEquals(timestamp.toLocalDate(), result.toLocalDate());
    Assertions.assertEquals(timestamp.toLocalTime().getHour(), result.toLocalTime().getHour());
    Assertions.assertEquals(0, resultMinute % timeValue);
    Assertions.assertEquals(0, result.toLocalTime().getSecond());
  }

  @ParameterizedTest
  @MethodSource("getRandomDateTimes")
  void getCandlestickDateTimeHourTimeFrame(LocalDateTime timestamp, int timeValue) {
    //given
    final TimeFrame timeFrame = Mockito.mock(TimeFrame.class);

    //when
    Mockito.when(timeFrame.getTimeValue()).thenReturn(timeValue);
    Mockito.when(timeFrame.getFrame()).thenReturn(Frame.HOUR);
    final LocalDateTime result = TimeFrameUtils.getCandlestickDateTime(timestamp, timeFrame);
    final int resultHour = result.toLocalTime().getHour();

    //then
    Assertions.assertEquals(timestamp.toLocalDate(), result.toLocalDate());
    Assertions.assertEquals(0, resultHour % timeValue);
    Assertions.assertEquals(0, result.toLocalTime().getMinute());
    Assertions.assertEquals(0, result.toLocalTime().getSecond());
  }

  @ParameterizedTest
  @MethodSource("getRandomDateTimes")
  void getCandlestickDateTimePlusDayTimeFrame(LocalDateTime timestamp, int timeValue) {
    //given
    final TimeFrame timeFrame = Mockito.mock(TimeFrame.class);

    //when
    Mockito.when(timeFrame.getTimeValue()).thenReturn(timeValue);
    Mockito.when(timeFrame.getFrame()).thenReturn(Frame.DAY);
    final LocalDateTime result = TimeFrameUtils.getCandlestickDateTime(timestamp, timeFrame);

    //then
    if (timestamp.getDayOfMonth() % timeFrame.getTimeValue() == 0) {
      Assertions.assertEquals(timestamp.getDayOfMonth() - (timeFrame.getTimeValue() - 1), result.toLocalDate().getDayOfMonth());
    } else {
      final int div = timestamp.getDayOfMonth() / timeFrame.getTimeValue();
      final int newDay = div * timeFrame.getTimeValue();
      Assertions.assertEquals(newDay + 1, result.toLocalDate().getDayOfMonth());
    }
    Assertions.assertEquals(timestamp.toLocalDate().getYear(), result.toLocalDate().getYear());
    Assertions.assertEquals(timestamp.toLocalDate().getMonth(), result.toLocalDate().getMonth());
    Assertions.assertEquals(0, result.toLocalTime().getHour());
    Assertions.assertEquals(0, result.toLocalTime().getMinute());
    Assertions.assertEquals(0, result.toLocalTime().getSecond());

  }
}