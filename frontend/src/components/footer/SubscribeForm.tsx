import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import './SubscribeForm.css';

const SubscribeForm = () => {
  const { t } = useTranslation();
  const [email, setEmail] = useState('');

  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setEmail(e.target.value);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    alert(t('footer.subscribe.form.alert', { email }));
  };

  return (
    <form className="newsletter-form" onSubmit={handleSubmit}>
      <input
        type="email"
        placeholder={t('footer.subscribe.form.placeholder')}
        value={email}
        onChange={handleEmailChange}
        className="email-input"
        required
      />
      <button className="sign-up-btn" type="submit">{t('footer.subscribe.form.button')}</button>
    </form>
  );
};

export default SubscribeForm;
