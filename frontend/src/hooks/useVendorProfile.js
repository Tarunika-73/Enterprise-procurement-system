import { useCallback, useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { findVendorByEmail, updateVendor } from '../services/vendorService';
import { getApiErrorMessage } from '../utils/apiErrors';

/**
 * Resolves the Vendor record tied to the currently logged-in vendor user
 * (matched by account email) and exposes helpers to refresh or update it.
 */
const useVendorProfile = () => {
  const { user } = useAuth();
  const [vendor, setVendor] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchVendor = useCallback(async () => {
    if (!user?.email) {
      setVendor(null);
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const match = await findVendorByEmail(user.email);
      setVendor(match);
      if (!match) {
        setError('No vendor profile is linked to this account yet. Contact your procurement officer.');
      }
    } catch (err) {
      setError(getApiErrorMessage(err, 'Unable to load your vendor profile right now.'));
    } finally {
      setIsLoading(false);
    }
  }, [user?.email]);

  useEffect(() => {
    fetchVendor();
  }, [fetchVendor]);

  const saveVendor = useCallback(
    async (payload) => {
      if (!vendor?.id) throw new Error('No vendor profile to update.');
      const response = await updateVendor(vendor.id, payload);
      const updated = response?.data ?? response;
      setVendor(updated);
      return updated;
    },
    [vendor?.id]
  );

  return { vendor, isLoading, error, refetch: fetchVendor, saveVendor };
};

export default useVendorProfile;
