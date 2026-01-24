import React, { useEffect, ChangeEvent, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { UseFormReturn } from 'react-hook-form';
import { useNavigate } from 'react-router';
import { upload, FileData } from 'app/common/file-upload';
import { handleServerError } from 'app/common/utils';
import InputErrors from 'app/common/input-row/input-errors';


export default function InputRow({ useFormResult, object, field, type = 'text',
    required = false, disabled = false, inputClass = '', options, downloadLink }: InputRowParams) {
  const { t } = useTranslation();
  const label = t(object + '.' + field + '.label') + (required ? '*' : '');

  const { register, setValue, formState: { errors }, watch } = useFormResult;
  let optionsMap = new Map();
  if (options && options instanceof Map) {
    optionsMap = options;
  } else if (options) {
    optionsMap = new Map(Object.entries(options));
  }
  const navigate = useNavigate();
  const currentValue = watch(field);
  const [currentFile, setCurrentFile] = useState<FileData|null>(null);
  const [withDownloads, setWithDownloads] = useState(true);

  const getInputClasses = () => {
    return (errors[field] ? 'border-red-600 ' : '') + (disabled ? 'bg-gray-100 ' : '') + inputClass;
  };

  const fileDelete = () => {
    setValue(field, null);
    setWithDownloads(false);
  };

  const fileChanged = async (event: ChangeEvent<HTMLInputElement>) => {
    const $filesInput = event.target as HTMLInputElement;
    setValue(field, null);
    if (!$filesInput.files) {
      return;
    }
    try {
      const uploadedFile = await upload($filesInput.files[0]!);
      setValue(field, JSON.stringify(uploadedFile));
      $filesInput.value = '';
    } catch (error: any) {
      handleServerError(error, navigate);
    }
  };

  if (type === 'file') {
    useEffect(() => {
      setCurrentFile(JSON.parse(currentValue || null));
    }, [currentValue]);
  }

  if (type === 'radio' && !required) {
    useEffect(() => {
      setValue(field, '');
    }, []);
  }

  return (
    <div className="md:grid grid-cols-12 gap-4 mb-4">
      {type === 'checkbox' ? (
        <div className="col-span-10 col-start-3">
          <div>
            <input id={field} {...register(field)} type="checkbox" disabled={disabled}
                className={'rounded border-gray-300 ' + getInputClasses()} />
            <label htmlFor={field} className="ml-2">
              {label}
            </label>
          </div>
          <InputErrors errors={errors} field={field} />
        </div>
      ) : (<>
      <label htmlFor={field} className="col-span-2 py-2">
        {label}
      </label>
      <div className="col-span-10">
        {type === 'text' || type === 'password' || type === 'email' || type === 'tel' || type === 'number' ? (
        <input id={field} {...register(field)} type={type} disabled={disabled}
            className={'w-full xl:w-3/4 border-gray-300 rounded ' + getInputClasses()} />
        ) : type === 'textarea' ? (
        <textarea id={field} {...register(field)} disabled={disabled}
            className={'w-full xl:w-3/4 border-gray-300 rounded ' + getInputClasses()}></textarea>
        ) : type === 'select' || type === 'multiselect' ? (
        <select id={field} {...register(field)} multiple={type === 'multiselect'} disabled={disabled}
            className={'w-full xl:w-3/4 border-gray-300 rounded ' + getInputClasses()}>
          {type === 'select' && <option value="">{t('select.empty.label')}</option>}
          {Array.from(optionsMap).map(([key, value]) => (
          <option value={key} key={key}>{value}</option>
          ))}
        </select>
        ) : type === 'radio' ? (<>
        {!required &&
          <div className="inline-block mr-4 last:mr-0 pt-2">
            <input id={field} {...register(field)} value="" type="radio" disabled={disabled}
                className={'border-gray-300 ' + getInputClasses()} />
            <label htmlFor={field} className="ml-2">{t('select.empty.label')}</label>
          </div>
        }
        {Array.from(optionsMap).map(([key, value]) => (
        <div key={key} className="inline-block mr-4 last:mr-0 pt-2">
          <input id={field + '_' + key} {...register(field)} value={key} type="radio" disabled={disabled}
              className={'border-gray-300 ' + getInputClasses()} />
          <label htmlFor={field + '_' + key} className="ml-2">{value}</label>
        </div>
        ))}
        </>) : type === 'file' ? (<>
        <input id={field} {...register(field)} type="hidden" disabled={disabled} />
        <input id={field + '_file'} type="file" disabled={disabled} onChange={fileChanged}
            className={'w-full xl:w-3/4 border-gray-300 rounded ' + (currentFile === null ? '' : 'hidden ') + getInputClasses()} />
        {currentFile !== null &&
          <div className="bg-transparent border-0 px-0 py-2 text-gray-900 flex items-baseline gap-2">
            <span>{currentFile.fileName}</span>
            {withDownloads && downloadLink &&
              <a href={process.env.API_PATH + downloadLink + '/' + currentFile!.fileName} target="_blank" className="text-sm text-gray-600 underline">{t('file.open')}</a>
            }
            <span role="button" onClick={fileDelete} className="text-sm text-gray-600 underline cursor-pointer">{t('file.delete')}</span>
          </div>
        }
        </>) : (<></>)}
        <InputErrors errors={errors} field={field} />
      </div>
      </>)}
    </div>
  );
}

interface InputRowParams {

  useFormResult: UseFormReturn<any, any, any|undefined>;
  object: string;
  field: string;
  type?: string;
  required?: boolean;
  disabled?: boolean;
  inputClass?: string;
  options?: Record<string, string>|Map<number, string>;
  downloadLink?: string;

}
