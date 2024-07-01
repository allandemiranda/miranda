package lu.forex.system.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Converter
public class DataTechnicalIndicatorConverter implements AttributeConverter<Map<String, Double>, String> {

  @Override
  public String convertToDatabaseColumn(final @NotNull Map<String, Double> attribute) {
    return attribute.keySet().stream().map(key -> key + "=" + attribute.get(key)).collect(Collectors.joining(", ", "{", "}"));
  }

  @Override
  public Map<String, Double> convertToEntityAttribute(final @NotNull String dbData) {
    if ("{}".equals(dbData)) {
      return new HashMap<String, Double>();
    } else {
      return Arrays.stream(dbData.substring(1, dbData.length() - 1).split(", ")).map(entry -> entry.split("="))
          .collect(Collectors.toMap(entry -> entry[0].replaceAll("\\s+", ""), entry -> Double.valueOf(entry[1])));
    }
  }
}
