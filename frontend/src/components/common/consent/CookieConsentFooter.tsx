import React from 'react';
import './CookieConsentFooter.css';
import LabAwayButton from '../button/LabAwayButton';
import { useConsent } from '../../../context/consent/ConsentProvider';
import { useTranslation } from "react-i18next";

interface Props {
    onOpenSettings: () => void;
}

const CookieConsentFooter: React.FC<Props> = ({ onOpenSettings }) => {
  const { t } = useTranslation();
  const { consent, setInteracted } = useConsent();

  if (consent.hasInteracted) return null;

  return (
    <div className="cookie-footer">
      <div className="cookie-message">
        {t("cookie.message")} <br />
        {t("cookie.info")}{" "}
        <button
          type="button"
          className="cookie-settings-link"
          onClick={onOpenSettings}
        >
          {t("cookie.settings")}
        </button>.
      </div>

      <LabAwayButton className="accept-button" onClick={setInteracted}>
        {t("cookie.accept")}
      </LabAwayButton>
    </div>
  );
};

export default CookieConsentFooter;
