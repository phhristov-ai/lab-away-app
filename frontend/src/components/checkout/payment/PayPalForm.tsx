import { PayPalScriptProvider, PayPalButtons } from '@paypal/react-paypal-js';

const PayPalForm: React.FC = () => {

    const getPayerName = (details: any): string => {
        return details.payment_source?.paypal?.name?.given_name ?? "Customer";
    };

    const PAYPAL_CLIENT_ID = "ATqelIzzL_dp6v70PBeMk62fAJRypK9QKSK_MIYHxqVcQZkY44csL6xFFXWdhasKpcyJkbwtc5RrVvqh";
    return (
        <PayPalScriptProvider options={{ clientId: PAYPAL_CLIENT_ID, currency: "EUR" }}>
            <PayPalButtons
                style={{ layout: "vertical", height: 45 }}
                createOrder={(data, actions) => {
                    return actions.order.create({
                        intent: "CAPTURE",
                        purchase_units: [
                            {
                                amount: {
                                    currency_code: "EUR",
                                    value: "25.00",
                                },
                            },
                        ],
                    });
                }}
                onApprove={async (data, actions) => {
                    if (actions.order) {
                      const details = await actions.order.capture();
                      const payerName = getPayerName(details);
                      alert(`Transaction completed by ${payerName}`);
                    }
                    return Promise.resolve();
                  }}
            />
        </PayPalScriptProvider>
    );
};

export default PayPalForm;
