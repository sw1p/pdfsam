/* 
 * This file is part of the PDF Split And Merge source code
 * Created on 16/lug/2014
 * Copyright 2013-2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pdfsam.ui.io;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.pdfsam.context.BooleanUserPreference;
import org.pdfsam.context.DefaultUserContext;
import org.pdfsam.test.ClearEventStudioRule;
import org.pdfsam.test.JavaFXThreadRule;
import org.pdfsam.ui.commons.SetDestinationRequest;

/**
 * @author Andrea Vacondio
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PdfDestinationPaneTest {

    private static final String MODULE = "MODULE";
    @Rule
    public JavaFXThreadRule fxThread = new JavaFXThreadRule();
    @Rule
    public ClearEventStudioRule clearStudio = new ClearEventStudioRule(MODULE);
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private BrowsablePdfInputField destination;
    private PdfDestinationPane victim;

    @Before
    public void setUp() {
        destination = spy(new BrowsablePdfInputField());
        victim = new PdfDestinationPane(destination, MODULE);
    }

    @Test
    public void setDestination() throws IOException {
        File footprint = folder.newFile("test.pdf");
        SetDestinationRequest event = SetDestinationRequest.requestDestination(footprint, MODULE);
        victim.setDestination(event);
        verify(destination).setTextFromFile(event.getFootprint());
    }

    @Test
    public void setFallbackDestination() throws IOException {
        destination.getTextField().setText("");
        DefaultUserContext.getInstance().setBooleanPreference(BooleanUserPreference.SMART_OUTPUT, true);
        File footprint = folder.newFile("test.pdf");
        SetDestinationRequest event = SetDestinationRequest.requestFallbackDestination(footprint, MODULE);
        victim.setDestination(event);
        verify(destination).setTextFromFile(event.getFootprint());
    }

    @Test
    public void dontSetFallbackDestinationIfFilled() throws IOException {
        destination.getTextField().setText("ChuckNorris");
        File footprint = folder.newFile("test.pdf");
        SetDestinationRequest event = SetDestinationRequest.requestDestination(footprint, MODULE);
        victim.setDestination(event);
        verify(destination).setTextFromFile(event.getFootprint());
    }

    @Test
    public void dontSetFallbackDestinationIfNoSmartOutput() throws IOException {
        DefaultUserContext.getInstance().setBooleanPreference(BooleanUserPreference.SMART_OUTPUT, false);
        File footprint = folder.newFile("test.pdf");
        SetDestinationRequest event = SetDestinationRequest.requestFallbackDestination(footprint, MODULE);
        victim.setDestination(event);
        verify(destination, never()).setTextFromFile(any());
    }
}