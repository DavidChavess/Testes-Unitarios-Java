

import static org.junit.Assert.assertSame;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

public class AssertTest {

	@Test
	public void test() {
		Assert.assertTrue(true);
		Assert.assertFalse(false);
		
		Assert.assertEquals(1.212, 1.215, 0.01);
		Assert.assertEquals(Math.PI, 3.14, 0.01);
		Assert.assertEquals("bola", "bola");
		Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
		
		Integer nI = 3;
		int n = 3;
		// Integer, Integer
		Assert.assertEquals(nI, Integer.valueOf(n));
		// int int 
		Assert.assertEquals(n, nI.intValue());
		
		Usuario u1 = new Usuario("usuario 1");
		Usuario u2 = new Usuario("usuario 1");
		
		// isso só funciona porque na classe usuario tem equals por nome
		Assert.assertEquals(u1,u2);
		
		assertSame(u2, u2);
		
		
		//Assert.assert
		
	}
}
