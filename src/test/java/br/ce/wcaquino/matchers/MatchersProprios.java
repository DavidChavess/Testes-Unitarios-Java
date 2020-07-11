package br.ce.wcaquino.matchers;

import java.util.Calendar;

public class MatchersProprios {
	public static DiaSemanaMatcher caiEm(Integer diaSemana) {
		return new DiaSemanaMatcher(diaSemana);
	}
	public static DiaSemanaMatcher caiNumaSegunda() {
		return new DiaSemanaMatcher(Calendar.MONDAY);
	}
	
	public static VerificaDataComDiferencaDiasMatcher ehHoje() {
		return new VerificaDataComDiferencaDiasMatcher(0);
	}
	
	public static VerificaDataComDiferencaDiasMatcher ehHojeComDiferencaDeUmDia() {
		return new VerificaDataComDiferencaDiasMatcher(1);
	}
	
	public static VerificaDataComDiferencaDiasMatcher ehHojeComDiferencaDeDias(Integer diferencaDias) {
		return new VerificaDataComDiferencaDiasMatcher(diferencaDias);
	}

}
