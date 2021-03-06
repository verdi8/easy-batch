/**
 * The MIT License
 *
 *   Copyright (c) 2019, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */
package org.easybatch.core.writer;

import org.easybatch.core.job.Job;
import org.easybatch.core.job.JobExecutor;
import org.easybatch.core.reader.StringRecordReader;
import org.easybatch.core.record.Batch;
import org.easybatch.core.record.Record;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.OutputStreamWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easybatch.core.job.JobBuilder.aNewJob;
import static org.easybatch.core.util.Utils.LINE_SEPARATOR;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link OutputStreamRecordWriter}.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class OutputStreamRecordWriterTest {

    public static final String PAYLOAD = "test";

    @Rule
    public final SystemOutRule systemOut = new SystemOutRule().enableLog();
    @Mock
    private OutputStreamWriter outputStreamWriter;
    @Mock
    private Record stringRecord;

    private OutputStreamRecordWriter outputStreamRecordWriter;

    @Before
    public void setUp() throws Exception {
        when(stringRecord.getPayload()).thenReturn(PAYLOAD);
        outputStreamRecordWriter = new OutputStreamRecordWriter(outputStreamWriter);
    }

    @Test
    public void testWriteRecords() throws Exception {
        outputStreamRecordWriter.writeRecords(new Batch(stringRecord));

        verify(outputStreamWriter).write(PAYLOAD);
        verify(outputStreamWriter).write(LINE_SEPARATOR);
        verify(outputStreamWriter).flush();
    }

    @Test
    public void outputStreamRecordWriterIntegrationTest() throws Exception {
        String dataSource = "1,foo" + LINE_SEPARATOR + "2,bar";

        Job job = aNewJob()
                .reader(new StringRecordReader(dataSource))
                .writer(new OutputStreamRecordWriter(new OutputStreamWriter(System.out)))
                .build();

        new JobExecutor().execute(job);

        // Assert that records have been written to System.out
        assertThat(systemOut.getLog()).isEqualTo(dataSource + LINE_SEPARATOR);
    }
}
