# Apache POI-Word

## 常用api

~~~java
// 获得表格中指定行，指定单元格的数据
table.getRow(2).getCell(1).getText()
// 设置表格中指定行，指定单元格的数据
table.getRow(6).getCell(0).setText(message);
table.getRow(8).getCell(1).setText("第八行 第二列");
~~~



## 指定位置新增行

~~~java

/**
 * 为表格插入数据，行数不够添加新行
 *
 * @param table     需要插入数据的表格
 * @param tableList 插入数据集合
 */
public static void insertTable(XWPFTable table, List<String[]> tableList) {
    //table.addNewRowBetween 没实现，官网文档也说明，只有函数名，但没具体实现，但很多文章还介绍如何使用这个函数，真是害人
    //table.insertNewTableRow 本文用这个可以，但是要创建 cell，否则不显示数据
    //table.addRow() 在表格最后加一行
    // table.addRow(XWPFTableRow row, int pos) 没试过，你可以试试。
    //table.createRow() 在表格最后一加行
 
    for (int i = 0; i < tableList.size(); i++) {//遍历要添加的数据的list
        XWPFTableRow newRow = table.insertNewTableRow(i+1);//为表格添加行
        String[] strings =  tableList.get(i);//获取list中的字符串数组
        for (int j = 0; j < strings.length; j++) {//遍历list中的字符串数组
            String strings1 =  strings[j];
            newRow.createCell();//在新增的行上面创建cell
            newRow.getCell(j).setText(strings1);//给每个cell赋值。
 
        }

————————————————
版权声明：本文为CSDN博主「jattxgt」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/qq_38555490/article/details/118156644
~~~

## 单元格合并

~~~java
public class Test {
 
	public static void main(String[] args) {
		XWPFDocument document = new XWPFDocument();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File("I:\\test.docx"));
			// 创建一个11行11列的表格
			XWPFTable table = document.createTable(11, 11);
			// 表格宽度
			CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
			width.setW(BigInteger.valueOf(2000));
 
			//列合并参数：表格，行，开始列，结束列
			mergeCellsHorizontal(table, 0, 1, 2);
			//列合并参数：表格，行，开始列，结束列
			mergeCellsHorizontal(table, 0, 4, 5);
			//列合并参数：表格，行，开始列，结束列
			mergeCellsHorizontal(table, 0, 7, 8);
 
			//行合并参数：表格，列，开始行，结束行
			mergeCellsVertically(table, 0, 1, 10);
 
			document.write(fos);
			System.out.println("successully");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
					document.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
 
	/**
	 * word单元格列合并
	 * @param table 表格
	 * @param row 合并列所在行
	 * @param startCell 开始列
	 * @param endCell 结束列
	 * @date 2020年4月8日 下午4:43:54
	 */
	public static void mergeCellsHorizontal(XWPFTable table, int row, int startCell, int endCell) {
		for (int i = startCell; i <= endCell; i++) {
			XWPFTableCell cell = table.getRow(row).getCell(i);
			if (i == startCell) {
				// The first merged cell is set with RESTART merge value  
				cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
			} else {
				// Cells which join (merge) the first one, are set with CONTINUE  
				cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
			}
		}
	}
 
	/**
	 * word单元格行合并
	 * @param table 表格
	 * @param col 合并行所在列
	 * @param fromRow 开始行
	 * @param toRow 结束行
	 * @date 2020年4月8日 下午4:46:18
	 */
	public static void mergeCellsVertically(XWPFTable table, int col, int startRow, int endRow) {
		for (int i = startRow; i <= endRow; i++) {
			XWPFTableCell cell = table.getRow(i).getCell(col);
			if (i == startRow) {
				// The first merged cell is set with RESTART merge value  
				cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
			} else {
				// Cells which join (merge) the first one, are set with CONTINUE  
				cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
			}
		}
	}
 
}
————————————————
版权声明：本文为CSDN博主「小百菜」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/u014644574/article/details/105391741
~~~

## 遍历表格中的数据

~~~java
		for (int h = 0; h < tables.size(); h++) {
            System.out.println("-------------第" + h + "表--------------");
            XWPFTable table = tables.get(h);
            List<XWPFTableRow> rows = table.getRows();
            for (int i = 0; i < rows.size(); i++) {
                System.out.print("现在是第" + i + "行--");
                XWPFTableRow xwpfTableRow = rows.get(i);
                List<XWPFTableCell> tableCells = xwpfTableRow.getTableCells();
                for (XWPFTableCell tableCell : tableCells) {
                    System.out.print(tableCell.getText() + "  ");
                }
                System.out.println();
            }
        }
~~~



