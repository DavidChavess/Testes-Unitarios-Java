package br.ce.wcaquino.matchers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.wcaquino.utils.DataUtils;

public class VerificaDataComDiferencaDiasMatcher extends TypeSafeMatcher<Date> {
	
	private Integer diferencaDias;
	
	public VerificaDataComDiferencaDiasMatcher(Integer diferencaDias) {
		this.diferencaDias = diferencaDias;
	}

	@Override
	public void describeTo(Description description) {
		Date dataEsperada = DataUtils.obterDataComDiferencaDias(diferencaDias);
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		description.appendText(sdf.format(dataEsperada));
	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtils.isMesmaData(data, DataUtils.obterDataComDiferencaDias(diferencaDias));
	}

}
