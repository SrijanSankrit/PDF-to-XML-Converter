# PDF-to-XML-Converter
# Project under CITI Summer Intern Pune 

### **Team Members**

  1. Srijan Sankrit (IITG)
  2. Bhavya Sharma (IITG)
  3. Kushagra Pandey (IITG)

##  **Idea**
 
To extract business information from a PDF document and convert it into an XML document.

#### **Product Description**

Our product can be used to convert various business documents, with support for multiple page PDF conversion and password-encrypted PDF conversion. It uses a config file for detecting a table in a typical invoice.
User has the option to supply his custom words before extraction.

Not a standard invoice? Don't worry, Our product is capable of converting non-standard invoice document into proper structured XML Document.


## **Libraries used**

[![PdfBox Version](https://img.shields.io/badge/pdfbox-20.0.19-brightgreen.svg)](https://pdfbox.apache.org/index.html)
[![JDK Version](https://img.shields.io/badge/JDK-14-brightgreen.svg)](https://docs.oracle.com/en/java/javase/14/)
[![XML_DOM Version](https://img.shields.io/badge/JAXP-1.4.2-brightgreen.svg)](https://mvnrepository.com/artifact/javax.xml/jaxp-api/1.4.2)
[![SWT Version](https://img.shields.io/badge/WindowBuilder-1.9.2-brightgreen.svg)](https://projects.eclipse.org/projects/tools.windowbuilder/releases/1.9.2)
[![Log4j2 Version](https://img.shields.io/badge/WindowBuilder-1.9.2-brightgreen.svg)](https://projects.eclipse.org/projects/tools.windowbuilder/releases/1.9.2)
[![SWT Version](https://img.shields.io/badge/Log4j2-2.11.1-brightgreen.svg)](https://logging.apache.org/log4j/2.x/)

## Team-1

## GUI and Command Line Usage

Provided for easy usage and reduced errors in file.

## Instructions For GUI

1. Open the folder named **Converter** which has **PDFtoXML.jar** file along with some properties files.
2. User has option to edit the keywords for table extraction inside the **config.properties** file.
3. Open the jar file named PDFtoXML.jar
4. Select the PDF file to be extracted by 'Choose' button.
5. Select the pages to be extracted or enter * for all pages in the file.
6. Add the password only if required.
7. Generate the XML in the same folder as of JAR by simply clicking on 'Generate XML', with name designating the time of creation, along with a log file generated in the same directory named **LogToFile**.
