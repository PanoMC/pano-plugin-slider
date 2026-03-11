<style>
  .slider-preview {
    width: 80px;
    height: 45px;
    overflow: hidden;
  }
  .slider-preview img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  .slider-preview-placeholder {
    width: 80px;
    height: 45px;
  }
  .btn-icon {
    width: 32px;
    height: 32px;
    padding: 0;
    line-height: 32px;
    text-align: center;
    border-radius: 8px;
  }
  .dragging {
    opacity: 0.4;
    background-color: var(--bs-light);
  }
  .clickable {
    cursor: pointer;
  }
</style>

<th scope="row" class="align-middle text-center" style="width: 50px;">
  <div class="dropdown position-static">
    <button
      type="button"
      class="btn btn-link"
      data-bs-toggle="dropdown"
      title={$_('pages.slider.table.actions')}
      use:tooltip={[$_('pages.slider.table.actions'), { placement: 'bottom' }]}
      aria-label={$_('pages.slider.table.actions')}>
      <span class="fas fa-ellipsis-v"></span>
    </button>
    <div class="dropdown-menu dropdown-menu-start animate__animated animate__fadeIn">
      <button
        type="button"
        class="dropdown-item"
        on:click={() => onEditClick(slider.id)}
        disabled={buttonsLoading}>
        <i class="fas fa-edit me-2"></i>
        {$_('buttons.edit')}
      </button>
      <button
        type="button"
        class="dropdown-item link-danger"
        on:click={() => onDeleteClick(slider.id)}
        disabled={buttonsLoading}>
        <i class="fas fa-trash me-2"></i>
        {$_('buttons.delete')}
      </button>
    </div>
  </div>
</th>
<td
  class="align-middle text-center p-0"
  style="width: 40px; cursor: grab;"
  use:tooltip={[$_('buttons.reorder'), { placement: 'bottom' }]}
  aria-label={$_('buttons.reorder')}>
  <i class="fas fa-grip-vertical"></i>
</td>
<td
  class="align-middle clickable"
  on:click={() => onEditClick(slider.id)}
  use:tooltip={[$_('buttons.edit'), { placement: 'bottom' }]}
  aria-label={$_('buttons.edit')}>
  {#if slider.imageUrl}
    <div class="slider-preview">
      <img
        src={slider.imageUrl.startsWith('http')
          ? slider.imageUrl
          : `${base}${slider.imageUrl}${slider.imageUrl.includes('?') ? '&' : '?'}thumbnail=true`}
        alt={slider.title}
        class="rounded" />
    </div>
  {:else}
    <div
      class="slider-preview-placeholder rounded d-flex align-middle justify-content-center align-items-center">
      <i class="fas fa-image"></i>
    </div>
  {/if}
</td>
<td class="align-middle" style="max-width: 250px;">
  <div class="text-truncate">
    <button
      type="button"
      class="btn btn-link p-0 text-start text-decoration-none w-100 text-truncate"
      on:click={() => onEditClick(slider.id)}
      use:tooltip={[$_('buttons.edit'), { placement: 'bottom' }]}>
      <span class="fw-bold d-block text-truncate">{slider.title}</span>
      {#if slider.subtitle}
        <div class="small text-truncate">{slider.subtitle}</div>
      {/if}
    </button>
  </div>
</td>
<td class="align-middle text-center">
  {slider.itemOrder}
</td>
<td class="align-middle text-center">
  <span class="badge {statusClass}">{statusText}</span>
</td>
<td class="align-middle">
  <div class="small">
    <Date time={slider.createdAt} />
  </div>
</td>
<td class="align-middle">
  <div class="small">
    <Date time={slider.updatedAt} />
  </div>
</td>

<script>
  import { _ } from '../../main';
  import { base } from '@panomc/sdk/svelte';
  import { Date } from '@panomc/sdk/components/panel';
  import tooltip from '@panomc/sdk/utils/tooltip';

  export let slider;
  export let onEditClick;
  export let onDeleteClick;
  export let buttonsLoading = false;

  $: statusClass = slider.active ? 'text-bg-success' : 'text-bg-primary';
  $: statusText = slider.active
    ? $_('pages.slider.table.status-active')
    : $_('pages.slider.table.status-inactive');
</script>
