import { Chip, Grid } from '@mui/material';
import { styled } from '@mui/material/styles';
import { TagOption } from '../../global/types';

const ListItem = styled('li')(({ theme }) => ({
  marginRight: theme.spacing(0.5),
  marginTop: theme.spacing(1),
}));

export default function TagDisplayBar(props: { tags: TagOption[] }) {
  return (
    <Grid
      sx={{
        display: 'flex',
        justifyContent: 'start',
        flexWrap: 'wrap',
        listStyle: 'none',
        p: 0,
        mb: 2,
      }}
      component="ul"
    >
      {props.tags.map(data => {
        return (
          <ListItem key={data.value}>
            <Chip
              label={data.label}
              variant="outlined"
              size="small"
              sx={{
                color: 'white',
                margin: 'auto',
                borderRadius: 3,
              }}
            />
          </ListItem>
        );
      })}
    </Grid>
  );
}
