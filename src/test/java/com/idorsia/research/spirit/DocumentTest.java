package com.idorsia.research.spirit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.util.CollectionUtils;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.model.Document;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DocumentTest extends AbstractSpiritTest {
	
	private static final int DOCUMENTID = 18160;

	@Test
	public final void aTestGet() {
		assertNotNull(getDocumentService().get(DOCUMENTID));
	}

	@Test
	public final void bTestList() {
		assertTrue(!CollectionUtils.isEmpty(getDocumentService().list()));
	}

	@Test
	public final void cTestGetCount() {
		assertTrue(getDocumentService().getCount()>0);
	}
	
	@Test
	public final void dTestAddDocument() {
		Document d = new Document();
		d.setId(getDocumentService().getSequence(Constants.DOCUMENT_SEQUENCE_NAME));
		d.setFilename("TEST.PSD");
		d.setType("PHOTOSHOP");
		d.setCreUser("benmeka1");
		d.setCreDate(new Date());
		//assertNotNull(getDocumentService().addDocument(d));
	}

	@Test
	public final void eTestSaveOrUpdate() {
		Document d = getLastInserted();
		d.setFilename("POC.PSD");
		getDocumentService().saveOrUpdate(d);
		assertTrue(getDocumentService().get(d.getId()).getFilename().equals("POC.PSD"));
	}

	@Test
	public final void fTestDelete() {
		int before = getDocumentService().getCount();
		try {
			getDocumentService().delete(getDocumentService().map(getLastInserted()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(before - getDocumentService().getCount() == 1);
	}
	
	private Document getLastInserted() {
		List<Document> list = getDocumentService().list();
		Collections.sort(list, new Comparator<Document>() {
			public int compare(Document d1, Document d2) {
				return Integer.compare(d2.getId(), d1.getId());
			}
		});
		Document a = list.get(0); // last inserted
		return a;
	}

}
