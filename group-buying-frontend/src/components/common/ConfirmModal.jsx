import React from "react";

const ConfirmModal = ({
                          isOpen,
                          message,
                          onConfirm,
                          onCancel,
                      }) => {
    if (!isOpen) return null;

    return (
        <div style={overlayStyle}>
            <div style={modalStyle}>
                <p style={messageStyle}>{message}</p>

                <div style={buttonWrapperStyle}>
                    <button style={confirmButtonStyle} onClick={onConfirm}>
                        확인
                    </button>
                    <button style={cancelButtonStyle} onClick={onCancel}>
                        취소
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmModal;

/* ================== styles ================== */

const overlayStyle = {
    position: "fixed",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: "rgba(0, 0, 0, 0.5)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    zIndex: 9999,
};

const modalStyle = {
    background: "#fff",
    padding: "24px",
    borderRadius: "8px",
    minWidth: "300px",
    textAlign: "center",
};

const messageStyle = {
    marginBottom: "20px",
    fontSize: "16px",
};

const buttonWrapperStyle = {
    display: "flex",
    justifyContent: "center",
    gap: "10px",
};

const confirmButtonStyle = {
    padding: "8px 16px",
    backgroundColor: "#007bff",
    color: "#fff",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer",
};

const cancelButtonStyle = {
    padding: "8px 16px",
    backgroundColor: "#ccc",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer",
};