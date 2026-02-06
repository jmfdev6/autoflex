import { createContext, useContext, useState, ReactNode, useEffect } from 'react';
import { en } from './locales/en';
import { ptBR } from './locales/pt-BR';

export type Locale = 'en' | 'pt-BR';

type Translations = typeof en;

interface I18nContextType {
  locale: Locale;
  setLocale: (locale: Locale) => void;
  t: Translations;
}

const translations: Record<Locale, Translations> = {
  en,
  'pt-BR': ptBR,
};

const I18nContext = createContext<I18nContextType | undefined>(undefined);

interface I18nProviderProps {
  children: ReactNode;
}

export const I18nProvider = ({ children }: I18nProviderProps) => {
  const [locale, setLocaleState] = useState<Locale>(() => {
    // Get saved locale from localStorage or default to 'pt-BR'
    const saved = localStorage.getItem('locale') as Locale;
    return saved && (saved === 'en' || saved === 'pt-BR') ? saved : 'pt-BR';
  });

  useEffect(() => {
    // Save locale to localStorage whenever it changes
    localStorage.setItem('locale', locale);
  }, [locale]);

  const setLocale = (newLocale: Locale) => {
    setLocaleState(newLocale);
  };

  const value: I18nContextType = {
    locale,
    setLocale,
    t: translations[locale],
  };

  return <I18nContext.Provider value={value}>{children}</I18nContext.Provider>;
};

export const useI18n = () => {
  const context = useContext(I18nContext);
  if (context === undefined) {
    throw new Error('useI18n must be used within an I18nProvider');
  }
  return context;
};
