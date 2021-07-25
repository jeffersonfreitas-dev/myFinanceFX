package test.model.service.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import database.DatabaseTest;
import database.exceptions.DatabaseException;
import model.dao.DAOMoviment;
import model.dao.impl.DAOMovimentImpl;
import model.entities.Moviment;


public class MovimentDAOTest {
	
	private Connection conn = DatabaseTest.getConnection();
	DAOMoviment dao = new DAOMovimentImpl(conn);
	
	

	
	@Test
	public void insert() {
		Moviment moviment = newEntity();
		Integer result = dao.insert(moviment);
		Integer id = 1;
		Moviment salved = dao.findById(id);
		assertNotNull(result);
		assertEquals(id, result);
		assertEquals("TESTE", salved.getName());
	}
	
	
	@Test(expected = DatabaseException.class)
	public void insertCamposNulos() {
		Moviment moviment = newEntity();
		moviment.setDateBeginner(null);
		moviment.setDateFinish(null);
		moviment.setName(null);
		moviment.setValueBeginner(null);
		dao.insert(moviment);
	}
	
	
	@Test
	public void update() {
		Integer id = 1;
		insert();
		Moviment mov = dao.findById(id);
		mov.setName("TESTE-ALTERADO");
		Integer result = dao.update(mov);
		Moviment salved = dao.findById(id);
		assertNotNull(result);
		assertEquals(id, result);
		assertEquals("TESTE-ALTERADO", salved.getName());
	}
	
	
	@Test(expected = DatabaseException.class)
	public void updateCamposNulos() {
		Integer id = 1;
		insert();
		Moviment mov = dao.findById(id);
		mov.setName("TESTE-ALTERADO");
		mov.setDateBeginner(null);
		dao.update(mov);
	}
	
	
	@Test(expected = DatabaseException.class)
	public void naoEncontrouIDParaAtualizar() {
		Moviment moviment = newEntity();
		Integer result = dao.insert(moviment);
		Moviment mov = dao.findById(result);
		mov.setId(100);
		dao.update(mov);
	}
	
	
	@Test
	public void findById() {
		insert();
		Moviment mov = dao.findById(1);
		assertEquals("TESTE", mov.getName());
		assertNotNull(mov);
	}
	
	@Test
	public void NaoEncountouIdParaPesquisa() {
		Moviment mov = dao.findById(1);
		assertNull(mov);
	}
	
	
	@Test
	public void findAllOrderByDateBeginner() {
		Moviment mov1 = newEntity();
		mov1.setClosed(false);
		Moviment mov2 = newEntity();
		Moviment mov3 = newEntity();
		Moviment mov4 = newEntity();
		
		Integer m1 = dao.insert(mov1);
		dao.insert(mov2);
		dao.insert(mov3);
		dao.insert(mov4);
		
		Moviment mv = dao.findById(m1);
		
		List<Moviment> movs = dao.findAllOrderByDateBeginner();
		assertNotNull(movs);
		assertEquals(4, movs.size());
		assertEquals("TESTE", mv.getName());
		assertFalse(mv.isClosed());
	}

	
	@Test
	public void findByAllOpenMoviment() {
		insert();
		Moviment mov = dao.findById(1);
		mov.setClosed(false);
		dao.update(mov);
		List<Moviment> movs = dao.findByAllOpenMoviment();
		
		assertNotNull(movs);
		assertEquals(1, movs.size());
	}
	
	
	@Test
	public void delete() {
		insert();
		Integer deletou = 1;
		Integer result = dao.deleteById(1);
		Moviment mov = dao.findById(1);
		assertEquals(deletou, result);
		assertNull(mov);
	}
		
	
	@Test(expected = DatabaseException.class)
	public void deleteNaoEncontrouId() {
		insert();
		dao.deleteById(100);
	}
	
	
	
	@Test
	public void RetornarNenhumMovimentoAberto() {
		List<Moviment> movs = dao.findByAllOpenMoviment();

		for(Moviment m : movs) {
			m.setClosed(true);
			dao.update(m);
		}
		
		List<Moviment> movs2 = dao.findByAllOpenMoviment();
		assertEquals(0, movs2.size());
	}
	
	
	@Test
	public void RetornaNenhumRegistro() {
		List<Moviment> movs = dao.findAllOrderByDateBeginner();
		assertEquals(0, movs.size());
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
	
	
	@Before
	public void createTables() {
		Statement stmt = null;
		String drop = "DROP TABLE IF EXISTS moviment";
		String sql = " CREATE OR REPLACE TABLE moviment ("
				+ "	id BIGINT auto_increment,"
				+ "	date_beginner date not null,"
				+ "	date_finish date not null,"
				+ "	closed boolean,"
				+ "	value_beginner decimal(10,2) not null,"
				+ "	value_finish decimal(10,2),"
				+ "	balance_moviment decimal(10,2),"
				+ "	name VARCHAR(50) not null,"
				+ "	constraint pk_moviment primary key (id)"
				+ ");";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(drop);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	@After
	public void dropTables() {
		Statement stmt = null;
		String drop = "DROP TABLE IF EXISTS moviment";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(drop);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
