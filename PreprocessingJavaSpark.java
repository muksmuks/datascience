		/**Code to filter out dataset columns which have all null values**/


    // If a columnName has a dot(.) in it, df.select does not like it. So need to
		// add tick (`)
		List<String> colNames = new ArrayList<String>();
		for (int i = 0; i < df.columns().length; i++) {
			String colName = df.columns()[i];
			colNames.add("`" + colName + "`");
		}

		final Seq<String> colNamesSeq = JavaConverters.asScalaBuffer(colNames).seq();
		Dataset<Row> dfStats = df.describe(colNamesSeq);

		Dataset<Row> dfStatsCount = dfStats.filter(col("summary").equalTo("count"));

		List<String> colNamesWithNonNullColumns = new ArrayList<String>();
		// ignore the first column value as it would be summary column with value
		// "count"
		for (int i = 1; i < dfStatsCount.columns().length; i++) {
			String colName = dfStatsCount.columns()[i];
			if (Integer.valueOf(dfStatsCount.select("`" + colName + "`").first().getString(0)) > 0) {
				colNamesWithNonNullColumns.add("`" + colName + "`");
			}
		}

  String firstColName = colNamesWithNonNullColumns.get(0);
  List<String> remainingColName = new ArrayList<String>();
  for (int i = 1; i < colNamesWithNonNullColumns.size(); i++) {
    String colName = colNamesWithNonNullColumns.get(i);
    remainingColName.add(colName);
  }

  Dataset<Row> dfWithNonNullColumns = df.select(firstColName,
      (String[]) remainingColName.toArray(new String[0]));
  logger.log(Level.FINE, "Column Count with Non null values" + dfWithNonNullColumns.columns().length);

  return dfWithNonNullColumns;
