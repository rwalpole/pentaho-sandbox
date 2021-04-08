import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import org.pentaho.di.core.xml.XMLCheck;

public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
  if (first) {
    first = false;
  }

  Object[] row = getRow();
  boolean rowInError = false;
  String errMsg = "";
  int errCnt = 0;

  if (row == null) {
    setOutputDone();
    return false;
  }
  Object[] outputRow = createOutputRow(row, data.outputRowMeta.size());

  final String xmlString = get(Fields.In, "xml_string").getString(row);

  InputStream xmlStream = null;
  try{
    xmlStream = new ByteArrayInputStream(xmlString.getBytes());
    if(!XMLCheck.isXMLWellFormed(xmlStream)){
      rowInError = true;
      errMsg = "The following XML is not well-formed: " + xmlString;
      errCnt = errCnt + 1;
    }
  }
  catch(Exception ex){
    rowInError = true;
    errMsg = ex.getMessage();
    errCnt = errCnt + 1;
  }
  finally {
    try{
	  if(xmlStream != null) xmlStream.close();
    }
    catch(IOException ioex){
      // closing quietly
    }
  } 

  if(!rowInError){
    putRow(data.outputRowMeta, row);
  } else {
    putError(data.outputRowMeta, row, errCnt, errMsg, "xml_string","ERROR_01");
  }

  return true;
}