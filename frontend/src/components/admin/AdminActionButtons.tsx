import Button from "../homepage/Button";
import ConfirmModal from "./ConfirmModal";
import SuccessModal from "./SuccessModal";

interface AdminActionButtonsProps {
  isNew: boolean;
  onSave: () => void;
  onDelete?: () => void;
  showDelete?: boolean;
  showConfirmDelete: boolean;
  onConfirmDelete: () => void;
  onCancelDelete: () => void;
  showSuccessModal: boolean;
  successMessage: string;
  onCloseSuccess: () => void;
}

const AdminActionButtons: React.FC<AdminActionButtonsProps> = ({
  isNew,
  onSave,
  onDelete,
  showDelete = true,
  showConfirmDelete,
  onConfirmDelete,
  onCancelDelete,
  showSuccessModal,
  successMessage,
  onCloseSuccess
}) => {
  return (
    <div className="button-group">
      <Button
        text={isNew ? "Create" : "Save"}
        onClick={onSave}
        className="save-button"
      />

      {!isNew && showDelete && onDelete && (
        <Button
          text="Delete"
          onClick={onDelete}
          className="delete-button"
        />
      )}

      <ConfirmModal
        show={showConfirmDelete}
        title="Delete"
        message="Are you sure you want to delete this item? This action cannot be undone."
        onConfirm={onConfirmDelete}
        onCancel={onCancelDelete}
        confirmText="Delete"
        cancelText="Cancel"
      />

      <SuccessModal
        show={showSuccessModal}
        message={successMessage}
        onClose={onCloseSuccess}
      />
    </div>
  );
};

export default AdminActionButtons;
