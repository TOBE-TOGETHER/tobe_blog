import MoreHoriz from '@mui/icons-material/MoreHoriz';
import { IconButton, Menu, MenuItem } from '@mui/material';
import React from 'react';
import { useTranslation } from 'react-i18next';
import { IBaseUserContentDTO, Operation } from '../../../global/types';

export default function CardHeaderActionButton(props: { operations: Operation[]; data: IBaseUserContentDTO; color?: string }) {
  const { t } = useTranslation();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const [anchorRecordId, setAnchorRecordId] = React.useState<null | string>(null);
  const open = Boolean(anchorEl);
  const handleClick = (event: React.MouseEvent<HTMLButtonElement>, id: string) => {
    setAnchorEl(event.currentTarget);
    setAnchorRecordId(id);
  };
  const handleClose = () => {
    setAnchorEl(null);
    setAnchorRecordId(null);
  };
  function getMenuItem(operationName: string) {
    switch (operationName) {
      case 'release':
        return t('components.standard-button.release');
      case 'delete':
        return t('components.standard-button.delete');
      case 'detail':
        return t('components.standard-button.detail');
    }
  }
  return (
    <>
      <IconButton
        aria-controls={open ? 'basic-menu' : undefined}
        aria-haspopup="true"
        aria-expanded={open ? 'true' : undefined}
        onClick={event => handleClick(event, props.data.id)}
      >
        <MoreHoriz
          sx={{
            color: props.color,
          }}
        />
      </IconButton>
      <Menu
        open={open && props.data.id === anchorRecordId}
        onClose={handleClose}
        anchorEl={anchorEl}
        MenuListProps={{
          'aria-labelledby': 'basic-button',
        }}
        anchorOrigin={{
          vertical: 'top',
          horizontal: 'left',
        }}
        transformOrigin={{
          vertical: 'bottom',
          horizontal: 'left',
        }}
      >
        {props.operations?.map(
          (operation, index) =>
            !operation?.hide?.call(null, props.data) && (
              <MenuItem
                key={props.data.id + '-' + index}
                onClick={() => operation.onClick(props.data.id)}
              >
                {getMenuItem(operation.name)}
              </MenuItem>
            )
        )}
      </Menu>
    </>
  );
}
