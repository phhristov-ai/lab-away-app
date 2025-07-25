import React from 'react';
import './ConfirmModal.css';

interface SuccessModalProps {
  title?: string;
  message: string;
  onClose: () => void;
  show: boolean;
}

const SuccessModal: React.FC<SuccessModalProps> = ({
  title = 'Success',
  message,
  onClose,
  show,
}) => {
  if (!show) return null;

  return (
    <div className="modal-overlay">
      <div className="modal">
        <h3>{title}</h3>
        <p>{message}</p>
        <div className="modal-buttons">
          <button className="confirm-btn" onClick={onClose}>OK</button>
        </div>
      </div>
    </div>
  );
};

export default SuccessModal;
