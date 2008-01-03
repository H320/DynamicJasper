/*
 * Dynamic Jasper: A library for creating reports dynamically by specifying
 * columns, groups, styles, etc. at runtime. It also saves a lot of development
 * time in many cases! (http://sourceforge.net/projects/dynamicjasper)
 *
 * Copyright (C) 2008  FDV Solutions (http://www.fdvsolutions.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 *
 * License as published by the Free Software Foundation; either
 *
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *
 */

package ar.com.fdvs.dj.test.subreport;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.test.ReportExporter;
import ar.com.fdvs.dj.test.TestRepositoryProducts;
import ar.com.fdvs.dj.util.SortUtils;
import junit.framework.TestCase;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class SubReportTest extends TestCase {

	public DynamicReport buildReport() throws Exception {


		FastReportBuilder drb = new FastReportBuilder();
		drb.addColumn("State", "state", String.class.getName(),30)
			.addColumn("Branch", "branch", String.class.getName(),30)
			.addColumn("Product Line", "productLine", String.class.getName(),50)
			.addColumn("Item", "item", String.class.getName(),50)
			.addColumn("Item Code", "id", Long.class.getName(),30,true)
			.addColumn("Quantity", "quantity", Long.class.getName(),60,true)
			.addColumn("Amount", "amount", Float.class.getName(),70,true)
			.addGroups(2)
			.setMargins(5, 5, 20, 20)
			.setTitle("November 2006 sales report")
			.setSubtitle("This report was generated at " + new Date())
			.setUseFullPageWidth(true);

		drb.addField("statistics", List.class.getName());

		DynamicReport drHeaderSubreport = createHeaderSubreport();
		drb.addSubreportInGroupHeader(2, drHeaderSubreport, new ClassicLayoutManager(),
				"statistics", DJConstants.SUBREPORT_DATA_SOURCE_ORIGIN_FIELD, DJConstants.DATA_SOURCE_TYPE_COLLECTION);

		 DynamicReport drFooterSubreport = createFooterSubreport();
		 drb.addSubreportInGroupFooter(2, drFooterSubreport,  new ClassicLayoutManager(),
				 "statistics", DJConstants.SUBREPORT_DATA_SOURCE_ORIGIN_FIELD, DJConstants.DATA_SOURCE_TYPE_COLLECTION);

		drb.setUseFullPageWidth(true);

		DynamicReport dr = drb.build();
		return dr;
	}

	private DynamicReport createHeaderSubreport() throws Exception {
		FastReportBuilder rb = new FastReportBuilder();
		DynamicReport dr = rb
			.addColumn("Date", "date", Date.class.getName(), 100)
			.addColumn("Average", "average", Float.class.getName(), 50)
			.addColumn("%", "percentage", Float.class.getName(), 50)
			.addColumn("Amount", "amount", Float.class.getName(), 50)
			.setMargins(5, 5, 20, 20)
			.setUseFullPageWidth(true)
			.setTitle("Subreport for this group")
			.build();
		return dr;
	}

	private DynamicReport createFooterSubreport() throws Exception {
		FastReportBuilder rb = new FastReportBuilder();
		DynamicReport dr = rb
		.addColumn("Area", "name", String.class.getName(), 100)
		.addColumn("Average", "average", Float.class.getName(), 50)
		.addColumn("%", "percentage", Float.class.getName(), 50)
		.addColumn("Amount", "amount", Float.class.getName(), 50)
		.addGroups(1)
		.setMargins(5, 5, 20, 20)
		.setUseFullPageWidth(true)
		.setTitle("Subreport for this group")
		.build();
		return dr;
	}

	public void testReport() {
	try {
		DynamicReport dr = buildReport();
		Collection dummyCollection = TestRepositoryProducts.getDummyCollection();
		dummyCollection = SortUtils.sortCollection(dummyCollection,dr.getColumns());

		JRDataSource ds = new JRBeanCollectionDataSource(dummyCollection);
		JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);
		ReportExporter.exportReport(jp, System.getProperty("user.dir")+ "/target/SubReportTest.pdf");
		JasperViewer.viewReport(jp);
//		JasperDesignViewer.viewReportDesign(DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager()));
	} catch (Exception e) {
		e.printStackTrace();
	}
}

	public static void main(String[] args) {
		SubReportTest test = new SubReportTest();
		test.testReport();
	}

}