/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.pdmodel.edit;

import junit.framework.TestCase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.operator.Operator;
import org.apache.pdfbox.util.operator.Operator;

import java.io.IOException;

/**
 * @author Yegor Kozlov
 */
public class TestPDPageContentStream extends TestCase
{
    public void testSetCmykColors() throws IOException
    {
        PDDocument doc = new PDDocument();

        PDPage page = new PDPage();
        doc.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(doc, page, false, true);
        // pass a non-stroking color in CMYK color space
        contentStream.setNonStrokingColor(0.1f, 0.2f, 0.3f, 0.4f);
        contentStream.close();

        // now read the PDF stream and verify that the CMYK values are correct
        COSStream stream = page.getContents().getStream();
        PDFStreamParser parser = new PDFStreamParser(stream);
        parser.parse();
        java.util.List<Object>  pageTokens = parser.getTokens();
        // expected five tokens :
        // [0] = COSFloat{0.1}
        // [1] = COSFloat{0.2}
        // [2] = COSFloat{0.3}
        // [3] = COSFloat{0.4}
        // [4] = PDFOperator{"k"}
        assertEquals(0.1f, ((COSFloat)pageTokens.get(0)).floatValue());
        assertEquals(0.2f, ((COSFloat)pageTokens.get(1)).floatValue());
        assertEquals(0.3f, ((COSFloat)pageTokens.get(2)).floatValue());
        assertEquals(0.4f, ((COSFloat)pageTokens.get(3)).floatValue());
        assertEquals("k", ((Operator) pageTokens.get(4)).getOperation());

        // same as above but for PDPageContentStream#setStrokingColor
        page = new PDPage();
        doc.addPage(page);

        contentStream = new PDPageContentStream(doc, page, false, false);
        // pass a non-stroking color in CMYK color space
        contentStream.setStrokingColor(0.5f, 0.6f, 0.7f, 0.8f);
        contentStream.close();

        // now read the PDF stream and verify that the CMYK values are correct
        stream = page.getContents().getStream();
        parser = new PDFStreamParser(stream);
        parser.parse();
        pageTokens = parser.getTokens();
        // expected five tokens  :
        // [0] = COSFloat{0.5}
        // [1] = COSFloat{0.6}
        // [2] = COSFloat{0.7}
        // [3] = COSFloat{0.8}
        // [4] = PDFOperator{"K"}
        assertEquals(0.5f, ((COSFloat)pageTokens.get(0)).floatValue());
        assertEquals(0.6f, ((COSFloat)pageTokens.get(1)).floatValue());
        assertEquals(0.7f, ((COSFloat)pageTokens.get(2)).floatValue());
        assertEquals(0.8f, ((COSFloat)pageTokens.get(3)).floatValue());
        assertEquals("K", ((Operator)pageTokens.get(4)).getOperation());
    }
}
