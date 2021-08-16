package test.model.service.moviment;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import model.dao.DAOMoviment;
import model.entities.Moviment;
import model.exceptions.RecordAlreadyRecordedException;
import model.exceptions.ValidationException;
import model.service.MovimentService;

@RunWith(MockitoJUnitRunner.class)
public class MovimentServiceTest {
	
	@InjectMocks
	private MovimentService service = new MovimentService();

	@Mock
	private DAOMoviment dao;
	
	@SuppressWarnings("deprecation")
	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
	

	
	@Test
	public void procurarTodosRegistros() {
		Moviment mov = newEntity();
		mov.setId(1);
		Moviment mov1 = newEntity();
		mov1.setId(2);
		List<Moviment> movs = new ArrayList<>();
		movs.add(mov);
		movs.add(mov1);
		Mockito.when(dao.findAllOrderByDateBeginner()).thenReturn(movs);

		List<Moviment> result = service.findAll();
		
		assertNotNull(result);
		assertEquals(2, result.size());
		Mockito.verify(dao, Mockito.times(1)).findAllOrderByDateBeginner();
	}
	
	
	@Test
	public void retornaListaVaziaQuandoNaoEncontraRegistros() {
		Mockito.when(dao.findAllOrderByDateBeginner()).thenReturn(new ArrayList<>());
		
		List<Moviment> result = service.findAll();
		
		assertNotNull(result);
		assertEquals(0, result.size());
		Mockito.verify(dao, Mockito.times(1)).findAllOrderByDateBeginner();
	}
	
	
	@Test(expected = RecordAlreadyRecordedException.class)
	public void retornaErroQuandoExistirMovimentoAberto() {
		Moviment mov = newEntity();
		mov.setId(1);
		List<Moviment> movs = new ArrayList<>();
		movs.add(mov);
		Mockito.when(dao.findByAllOpenMoviment()).thenReturn(movs);
		
		service.saveOrUpdate(mov);
	}
	
	
	@Test(expected = ValidationException.class)
	public void retornaErroQuandoObjetoParaSalvarEstaNulo() {
		Moviment mov = null;
		service.saveOrUpdate(mov);
	}
	
	
	
	
	private Moviment newEntity() {
		Moviment moviment = new Moviment();
		moviment.setBalanceMoviment(0.0);
		moviment.setClosed(true);
		moviment.setDateBeginner(Date.from(Instant.now()));
		moviment.setDateFinish(Date.from(Instant.now()));
		moviment.setName("TESTE");
		moviment.setValueBeginner(0.0);
		moviment.setValueFinish(0.0);
		return moviment;
	}	

}
