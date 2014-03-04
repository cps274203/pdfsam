/* 
 * This file is part of the PDF Split And Merge source code
 * Created on 03/mar/2014
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
package org.pdfsam.ui.selection;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.sejda.eventstudio.StaticStudio.eventStudio;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.layout.HBox;

import org.apache.commons.lang3.StringUtils;
import org.pdfsam.context.DefaultI18nContext;
import org.pdfsam.module.ModuleOwned;
import org.pdfsam.pdf.EncryptionStatus;
import org.pdfsam.pdf.PdfLoadRequestEvent;
import org.pdfsam.ui.support.Style;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;

/**
 * A popup displaying a password field to let the user input a document password
 * 
 * @author Andrea Vacondio
 *
 */
class PasswordFieldPopup extends PopupControl implements ModuleOwned {
    private String ownerModule = StringUtils.EMPTY;
    private PasswordFieldPopupContent content = new PasswordFieldPopupContent();
    private SelectionTableRowData data;

    public PasswordFieldPopup(SelectionTableRowData data, String ownerModule) {
        this.data = data;
        this.ownerModule = defaultString(ownerModule);
        getStyleClass().setAll("pdfsam-input-password");
        setAutoHide(true);
        setHideOnEscape(true);
        setAutoFix(true);

    }

    public String getOwnerModule() {
        return ownerModule;
    }

    PasswordFieldPopupContent getPopupContent() {
        return content;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PasswordFieldPopupSkin(this);
    }

    /**
     * Panel containing fields to let the user enter a pdf document user password
     * 
     * @author Andrea Vacondio
     *
     */
    private class PasswordFieldPopupContent extends HBox {
        private PasswordField passwordField = new PasswordField();

        public PasswordFieldPopupContent() {
            getStyleClass().setAll("pdfsam-input-password-content");
            passwordField.setPromptText(DefaultI18nContext.getInstance().i18n("Enter the user password"));
            Button doneButton = AwesomeDude.createIconButton(AwesomeIcon.UNLOCK,
                    DefaultI18nContext.getInstance().i18n("Unlock"));
            doneButton.getStyleClass().addAll(Style.BUTTON.css());
            doneButton.prefHeightProperty().bind(passwordField.heightProperty());
            doneButton.setMaxHeight(USE_PREF_SIZE);
            doneButton.setMinHeight(USE_PREF_SIZE);
            doneButton.setOnAction((e) -> requestLoad());
            passwordField.setOnAction((e) -> requestLoad());
            getChildren().addAll(passwordField, doneButton);
        }

        public void requestLoad() {
            if (data != null) {
                data.getDocumentDescriptor().setPassword(passwordField.getText());
                data.getDocumentDescriptor().setEncryptionStatus(EncryptionStatus.DECRYPTION_REQUESTED);
                PdfLoadRequestEvent loadEvent = new PdfLoadRequestEvent(getOwnerModule());
                loadEvent.add(data.getDocumentDescriptor());
                eventStudio().broadcast(loadEvent);
                passwordField.clear();
            }
            hide();
        }
    }
}
