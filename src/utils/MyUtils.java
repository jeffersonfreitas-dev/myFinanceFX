package utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class MyUtils {

	
	public static boolean dateInitialBeforeFinish(Date initial, Date finish) {
		if(initial == null || finish == null) {
			throw new IllegalStateException("Data inicial ou final nula");
		}
		LocalDate ini = initial.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate fim = finish.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		return ini.isBefore(fim) ? true : false;
	}
}
