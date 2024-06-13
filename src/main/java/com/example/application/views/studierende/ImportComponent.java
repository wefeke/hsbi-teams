//package com.example.application.views.studierende;
//
//import React, { Component } from "react";
//import axios from "axios";
//
//class ImportComponent extends Component {
//    state = {
//        file: null,
//                previewData: [],
//        showDialog: false
//    };
//
//    handleFileChange = (e) => {
//        this.setState({ file: e.target.files[0] });
//    };
//
//    handlePreview = async () => {
//        const formData = new FormData();
//        formData.append("file", this.state.file);
//
//        try {
//            const response = await axios.post("/import/preview", formData, {
//                    headers: {
//                "Content-Type": "multipart/form-data"
//            }
//            });
//
//            this.setState({ previewData: response.data, showDialog: true });
//        } catch (error) {
//            console.error("Fehler beim Laden der Vorschau-Daten:", error);
//        }
//    };
//
//    handleConfirm = async () => {
//        try {
//            await axios.post("/import/confirm", this.state.previewData, {
//                    headers: {
//                "Content-Type": "application/json"
//            }
//            });
//
//            this.setState({ showDialog: false });
//            alert("Daten erfolgreich importiert!");
//        } catch (error) {
//            console.error("Fehler beim Bestätigen der Daten:", error);
//        }
//    };
//
//    handleCancel = () => {
//        this.setState({ showDialog: false });
//    };
//
//    render() {
//        return (
//                <div>
//                <input type="file" onChange={this.handleFileChange} />
//                <button onClick={this.handlePreview}>Daten anzeigen</button>
//
//                {this.state.showDialog && (
//                        <div className="dialog">
//                <h2>Vorschau der Daten</h2>
//                        <ul>
//                {this.state.previewData.map((teilnehmer, index) => (
//                <li key={index}>{`${teilnehmer.vorname} ${teilnehmer.nachname}`}</li>
//                            ))}
//                        </ul>
//                        <button onClick={this.handleConfirm}>Bestätigen</button>
//                        <button onClick={this.handleCancel}>Abbrechen</button>
//                    </div>
//                )}
//            </div>
//        );
//    }
//}
//
//export default ImportComponent;
//
//
//}
