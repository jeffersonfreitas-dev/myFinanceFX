package utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class Utils {

	public static Stage getCurrentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}

	public static Scene getCurrentScene(ActionEvent event) {
		return ((Node) event.getSource()).getScene();
	}

	public static Integer tryParseToInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return null;
		}
	}

	public static void formatDatePicker(DatePicker datePicker, String format) {
		datePicker.setConverter(new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format);
			{
				datePicker.setPromptText(format.toLowerCase());
			}

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}

		});
	}

	public static <T> void formatTableColumnDate(TableColumn<T, Date> tableColumn, String format) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Date> cell = new TableCell<T, Date>() {
				private SimpleDateFormat sdf = new SimpleDateFormat(format);

				@Override
				protected void updateItem(Date item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						setText(sdf.format(item));
					}
				}
			};
			return cell;
		});
	}

	public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColumn, int decimalPlaces) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Double> cell = new TableCell<T, Double>() {
				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						setText(String.format("%." + decimalPlaces + "f", item));
					}
				}
			};
			return cell;
		});
	}

	public static <T> void formatTableColumnStatus(TableColumn<T, String> tableColumn, String status) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, String> cell = new TableCell<T, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						if (item.equals("ABERTO")) {
							setText("AB");
						} else if (item.equals("PAGO")) {
							setText("PG");
						} else {
							setText("OUT");
						}
					}
				}
			};
			return cell;
		});
	}

}
